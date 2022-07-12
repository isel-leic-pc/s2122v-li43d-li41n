package pt.isel.leic.pc.test1

import java.util.LinkedList
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

/**
 * Implement the message queue synchronizer, to support communication between producer and consumer threads
 * through messages of generic type T. The communication must use the FIFO criterion (first in first out).
 * The public interface of this synchronizer is as follows:
 *
 * class MessageQueue<T>() {
 *    fun enqueue(message: T): Unit { … }
 *    @Throws(InterruptedException::class)
 *    fun tryDequeue(nOfMessages: Int, timeout: Duration): List<T>? { … }
 * }
 *
 * The [enqueue] method delivers a message to the queue without blocking the caller thread. The [tryDequeue] method
 * attempts to remove [nOfMessages] messages from the queue, blocking the invoking thread as long as:
 * 1) this operation cannot complete successfully, or
 * 2) the [timeout] time set for the operation does not expire, or
 * 3) the thread is not interrupted.
 * Note that message removal cannot be performed partially, i.e. either [nOfMessages] messages are removed or
 * no messages are removed. These removal operations must be completed on a first-come, first-served basis,
 * regardless of the values [nOfMessages]. Be aware of the consequences of giving up (either through cancellation
 * or timeout) on a [tryDequeue] operation.
 */
class MessageQueue<T> {

    private val queue = LinkedList<T>()
    private val guard = ReentrantLock()

    private class WaitingNode<T>(val nOfMessages: Int, val condition: Condition) {
        var messages: List<T>? = null
    }
    private val waitingQueue = LinkedList<WaitingNode<T>>()


    fun enqueue(message: T) {
        guard.withLock {
            queue.addLast(message)
            maybeSignal()
        }
    }

    @Throws(InterruptedException::class)
    fun tryDequeue(nOfMessages: Int, timeout: Duration): List<T>? {
        require(nOfMessages > 0)
        guard.withLock {
            if (waitingQueue.isEmpty() && queue.size >= nOfMessages) {
                val myMessages = LinkedList<T>()
                repeat(nOfMessages) { myMessages.addLast(queue.removeFirst()) }
                return myMessages
            }

            val myNode = WaitingNode<T>(nOfMessages, guard.newCondition())
            waitingQueue.addLast(myNode)
            var remainingTime = timeout.inWholeNanoseconds

            try {
                while (true) {

                    remainingTime = myNode.condition.awaitNanos(remainingTime)

                    val myMessages = myNode.messages
                    if (myMessages != null)
                        return myMessages

                    if (remainingTime <= 0) {
                        waitingQueue.remove(myNode)
                        maybeSignal()
                        return null
                    }
                }
            } catch (ie: InterruptedException) {

                if (waitingQueue.remove(myNode)) {
                    maybeSignal()
                    throw ie
                }
                else {
                    Thread.currentThread().interrupt()
                    return myNode.messages
                }
            }
        }
    }

    private fun maybeSignal() {
        while(waitingQueue.isNotEmpty() && queue.size >= waitingQueue.first.nOfMessages) {
            val first = waitingQueue.removeFirst()
            val myMessages = LinkedList<T>()
            repeat(first.nOfMessages) { myMessages.addLast(queue.removeFirst()) }
            first.messages = myMessages
            first.condition.signal()
        }
    }
}

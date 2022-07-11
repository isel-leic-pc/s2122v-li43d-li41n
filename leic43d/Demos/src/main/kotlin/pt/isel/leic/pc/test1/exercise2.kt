package pt.isel.leic.pc.test1

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

    fun enqueue(message: T) {
        TODO()
    }

    @Throws(InterruptedException::class)
    fun tryDequeue(nOfMessages: Int, timeout: Duration): List<T>? {
        TODO()
    }
}

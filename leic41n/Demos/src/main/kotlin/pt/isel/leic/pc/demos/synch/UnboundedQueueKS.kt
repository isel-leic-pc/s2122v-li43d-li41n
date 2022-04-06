package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Class whose instances represent <i>unbounded queue</i>.
 *
 * An <i>unbounded buffer</i> is a synchronization object used to support communication
 * between threads that produce items to be used (consumed) by other threads. The former are
 * named <i>producers</i>, the latter are named <i>consumers</i>.
 *
 * This type of synchronization object is useful when the expected production rate is lower than
 * the rate of consumption. In such scenario, memory exhaustion provoked by the absence of bounds
 * does not occur.
 *
 * Notice that this implementation enforces a FIFO policy for servicing contending threads.
 */
class UnboundedQueueKS<T> {

    /**
     * The actual queue.
     */
    private val queue = mutableListOf<T>()

    private class Request<T>(var item: T? = null)
    private val requests = mutableListOf<Request<T>>()

    // The monitor's lock and condition
    private val mLock: Lock = ReentrantLock()
    private val mCondition: Condition = mLock.newCondition()

    private fun notifyBlockedThread(item: T) {
        val request = requests.removeFirst()
        request.item = item
        mCondition.signalAll()
    }

    /**
     * Adds the given element to the queue.
     * @param elem The element to be added to the queue
     */
    fun put(item: T) {
        mLock.withLock {

            if (requests.isEmpty()) {
                queue.add(item)
                return
            }
            notifyBlockedThread(item)
        }
    }

    /**
     * Removes an element from the queue. The calling thread is blocked until an element becomes available or
     * the specified time as elapsed.
     *
     * @return The element removed from the buffer or <code>null</code> if the specified time elapses
     * before an element becomes available.
     * @throws InterruptedException If the blocked thread has been signaled for cancellation.
     */
    @Throws(InterruptedException::class)
    fun take(timeout: Long, unit: TimeUnit): T? {
        mLock.withLock {

            if (queue.isNotEmpty())
                return queue.removeFirst()

            var remainingTime = unit.toNanos(timeout)
            val myRequest = Request<T>()
            requests.add(myRequest)

            while (true) {

                try {
                    remainingTime = mCondition.awaitNanos(remainingTime)
                }
                catch (ie: InterruptedException) {
                    requests.remove(myRequest)
                    throw ie
                }

                if (myRequest.item != null)
                    return myRequest.item

                if (remainingTime <= 0)
                    return null
            }
        }
    }
}
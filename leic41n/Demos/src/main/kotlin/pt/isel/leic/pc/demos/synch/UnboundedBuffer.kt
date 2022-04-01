package palbp.laboratory.demos.synch

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Class whose instances represent <i>unbounded buffers</i>.
 *
 * An <i>unbounded buffer</i> is a synchronization object used to support communication
 * between threads that produce items to be used (consumed) by other threads. The former are
 * named <i>producers</i>, the latter are named <i>consumers</i>.
 *
 * This type of synchronization object is useful when the expected production rate is lower than
 * the rate of consumption. In such scenario, memory exhaustion provoked by the absence of bounds
 * does not occur.
 *
 * Notice that this implementation does not enforce a specific policy for servicing contending threads.
 */
class UnboundedBuffer<T> {

    /**
     * The actual buffer.
     */
    private val buffer = mutableListOf<T>()

    // The monitor's lock and condition
    private val mLock: Lock = ReentrantLock()
    private val mCondition: Condition = mLock.newCondition()

    /**
     * Adds the given element to the buffer.
     * @param elem The element to be added to the buffer
     */
    fun put(item: T) {
        mLock.withLock {
            buffer.add(item)
            // Notify a blocked thread.
            // Note that for a thread to be blocked, the buffer can only have one element, the one we just added.
            if (buffer.size == 1)
                mCondition.signal()
        }
    }

    /**
     * Adds the given elements to the buffer.
     * @param elems The elements to be added to the buffer
     */
    fun putAll(items: Iterable<T>) {
        mLock.withLock {
            val wasEmpty = buffer.isEmpty()
            buffer.addAll(items)
            if (wasEmpty)
                mCondition.signalAll()      // This can be improved
        }
    }

    /**
     * Removes an element from the buffer. The calling thread is blocked until an element becomes available or
     * the specified time as elapsed.
     *
     * @return The element removed from the buffer or <code>null</code> if the specified time elapses
     * before an element becomes available.
     * @throws InterruptedException If the blocked thread has been signaled for cancellation.
     */
    @Throws(InterruptedException::class)
    fun take(timeout: Long, unit: TimeUnit): T? {
        mLock.withLock {

            // Check if there are any elements in the buffer.
            if (buffer.isNotEmpty())
                return buffer.removeFirst()

            // Otherwise, block calling thread until the required conditions are met
            var remainingTime = unit.toNanos(timeout)
            while (true) {
                remainingTime = mCondition.awaitNanos(remainingTime)

                if (buffer.isNotEmpty())
                    return buffer.removeFirst()          // Thread was signaled

                if (remainingTime <= 0)                 // Timeout
                    return null
            }
        }
    }
}
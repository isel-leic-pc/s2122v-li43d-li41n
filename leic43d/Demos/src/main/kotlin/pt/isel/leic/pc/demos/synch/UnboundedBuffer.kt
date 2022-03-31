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
     * Adds the given element to the buffer.
     * @param elem The element to be added to the buffer
     */
    fun put(item: T) {
        TODO()
    }

    /**
     * Adds the given elements to the buffer.
     * @param elems The elements to be added to the buffer
     */
    fun putAll(items: Iterable<T>) {
        TODO()
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
        TODO()
    }
}
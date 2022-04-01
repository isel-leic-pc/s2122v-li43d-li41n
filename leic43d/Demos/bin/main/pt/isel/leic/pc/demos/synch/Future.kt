package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * The contract for results that will be made available sometime in the future. Used as a result
 * of asynchronous operations when the caller waits explicitly for the result.
 */
interface AFuture<T> {

    /**
     * Gets the future's result. If the result is not yet available, blocks the calling thread until it's
     * interrupted or the result becomes available.
     * @throws InterruptedException if the blocked thread is cancelled while waiting for the result
     * @throws Throwable if the operation's result is an error
     * @return the operation's result
     */
    @Throws(InterruptedException::class, Throwable::class)
    fun get(): T

    /**
     * Gets the future's result. If the result is not yet available, blocks the calling thread until
     * the result becomes available, the blocked thread is cancelled or the specified time elapses.
     * @throws InterruptedException if the blocked thread is cancelled while waiting for the result
     * @throws Throwable if the operation's result is an error
     * @return the operation's result or null if the specified time has elapsed
     */
    @Throws(InterruptedException::class, Throwable::class)
    fun get(timeout: Long, unit: TimeUnit): T?

    /**
     * Indicates whether the result is already available or not
     */
    val isDone: Boolean
}

/**
 * A thread safe implementation of [AFuture]. Includes both the asynchronous operation caller interface ([AFuture])
 * and the set of methods required by the result producer ([setSuccess], [setFailure])
 */
class AFutureImpl<T> : AFuture<T> {

    // The future's possible results
    private var result: T? = null
    private var error: Throwable? = null

    // The monitor's lock and condition
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    @Throws(InterruptedException::class, Throwable::class)
    override fun get(): T {
        mLock.withLock {
            // Do we have a result?
            if (isDone) {
                return result ?: throw error as Throwable
            }

            while (true) {
                // There's no result yet. Let's block
                mCondition.await()

                // Do we have a result?
                if (isDone) {
                    return result ?: throw error as Throwable
                }
            }
        }
    }

    @Throws(InterruptedException::class)
    override fun get(timeout: Long, unit: TimeUnit): T? {
        mLock.withLock {
            // Do we have a result?
            if (isDone)
                return result ?: throw error as Throwable

            var remainingTime = unit.toNanos(timeout)
            while (true) {
                remainingTime = mCondition.awaitNanos(remainingTime)

                // Do we have a result?
                if (isDone)
                    return result ?: throw error as Throwable

                // Has the wait time expired?
                if (remainingTime <= 0)
                    return null
            }
        }
    }

    override var isDone: Boolean = false
        get() = mLock.withLock { field }
        // mLock must be held when accessing the property's setter
        private set

    /**
     * Publishes a successful result, unblocking all waiting threads.
     */
    fun setSuccess(result: T) {
        mLock.withLock {
            if (isDone) throw IllegalStateException()
            this.result = result
            isDone = true
            mCondition.signalAll()
        }
    }

    /**
     * Publishes an error result, unblocking all waiting threads.
     */
    fun setFailure(failure: Throwable) {
        mLock.withLock {
            if (isDone) throw IllegalStateException()
            this.error = failure
            isDone = true
            mCondition.signalAll()
        }
    }
}

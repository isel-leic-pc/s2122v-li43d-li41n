package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit

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

    @Throws(InterruptedException::class, Throwable::class)
    override fun get(): T {
        TODO()
    }

    @Throws(InterruptedException::class)
    override fun get(timeout: Long, unit: TimeUnit): T? {
        TODO()
    }

    override var isDone: Boolean
        get() = TODO()
        private set(value) = TODO()

    /**
     * Publishes a successful result, unblocking all waiting threads.
     */
    fun setSuccess(result: T) {
        TODO()
    }

    /**
     * Publishes an error result, unblocking all waiting threads.
     */
    fun setFailure(failure: Throwable) {
        TODO()
    }
}

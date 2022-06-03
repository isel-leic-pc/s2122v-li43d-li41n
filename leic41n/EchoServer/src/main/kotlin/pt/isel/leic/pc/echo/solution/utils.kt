package pt.isel.leic.pc.echo.solution

import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.LinkedList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * A non-blocking implementation of the traditional semaphore, that is, a unit manager.
 * The number of initial available units is specified as a construction parameter.
 */
class AsyncSemaphore(initialUnits: Int) {

    private var units = initialUnits
    private val guard = ReentrantLock()
    private val queue = LinkedList<Request>()

    private class Request : CompletableFuture<Unit>()

    /**
     * Asynchronously acquires a unit. The returned [CompletableFuture] instance becomes `completed`
     * when the unit becomes available.
     */
    fun acquire(): CompletableFuture<Unit> {
        guard.withLock {
            if (units != 0) {
                units -= 1
                return CompletableFuture.completedFuture(Unit)
            }

            val request = Request()
            queue.addLast(request)
            return request
        }
    }

    /**
     * Releases a unit, making it available for subsequent requests.
     */
    fun release() {
        guard.withLock {
            if (queue.isEmpty()) {
                units += 1
                null
            }
            else {
                queue.removeFirst()
            }
        }?.complete(Unit)
    }
}

/**
 * Extension function that suspends the calling coroutine until [this] [CompletableFuture] instance
 * is completed.
 *
 * @param   T The operation's result type
 * @return  The operation result
 * @throws  Throwable if the operation execution produced an error
 */
suspend fun <T> CompletableFuture<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        whenComplete { result, error ->
            if (error != null)
                continuation.resumeWithException(error)
            else
                continuation.resume(result)
        }
    }
}

package pt.isel.leic.pc.test1

import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.suspendCoroutine

/**
 * Consider the Exchanger synchronizer performed in the first set of exercises. Make a synchronizer with similar
 * functionality, but in which the exchange function is a suspending function, that is, it does not block the invoking
 * thread while waiting. For simplification, the function does not support timeout or cancellation.
 *
 * class Exchanger<T> {
 *    suspend fun exchange(value: T): T { … }
 * }
 *
 * This synchronizer supports the exchange of information between pairs of coroutines. Coroutines that use it,
 * manifest their availability to initiate an exchange by invoking the exchange method, specifying the object they
 * want to deliver to the partner coroutine (value). The exchange method ends up returning the exchanged value,
 * when the exchange is performed with another coroutine.
 */

interface ISuspendingExchanger<T> {
    suspend fun exchange(value: T): T
}

/**
 * In this implementation we start with an "asynchronizer" based in a [CompletableFuture] (see [internalExchange]) and
 * then we extend the solution to expose a suspending function that suspends the calling coroutine until the underlying
 * future becomes completed.
 */
class SuspendingExchangerAsPerMyClasses<T> : ISuspendingExchanger<T> {

    private fun internalExchange(value: T): CompletableFuture<T> {
        TODO()
    }

    override suspend fun exchange(value: T): T {
        TODO()
    }
}

/**
 * In this implementation we only make use of constructs that belong to the coroutines' concurrency model, namely,
 * [Mutex] and [suspendCoroutine].
 */
class SuspendingExchanger<T> : ISuspendingExchanger<T> {

    override suspend fun exchange(value: T): T {
        TODO()
    }
}

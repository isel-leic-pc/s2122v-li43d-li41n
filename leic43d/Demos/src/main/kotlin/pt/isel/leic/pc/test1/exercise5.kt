package pt.isel.leic.pc.test1

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 *  Implement the function with the following signature:
 *
 *  suspend fun race(f0: suspend () -> Int, f1: suspend () -> Int): Int
 *
 *  This function executes the functions passed as an argument in parallel, returning the value returned by the first
 *  function to finish successfully. An execution of the race function should only end when the coroutines created in
 *  its scope have finished. However, the race function must terminate as quickly as possible, by canceling the still
 *  running coroutine, after one of the functions passed as an argument has successfully completed.
 */

/**
 * The following solution leverages default cancellation behaviour.
 */
suspend fun race(f0: suspend () -> Int, f1: suspend () -> Int): Int {

    val guard = Mutex()
    var firstResult: Int? = null

    try {
        coroutineScope {
            val parent = this
            launch {
                val result = f0()
                guard.withLock {
                    if (firstResult == null) {
                        firstResult = result
                        parent.cancel()
                    }
                }
            }
            launch {
                val result = f1()
                guard.withLock {
                    if (firstResult == null) {
                        firstResult = result
                        parent.cancel()
                    }
                }
            }
        }
    }
    catch (_: CancellationException) { }
    return guard.withLock { firstResult as Int }
}

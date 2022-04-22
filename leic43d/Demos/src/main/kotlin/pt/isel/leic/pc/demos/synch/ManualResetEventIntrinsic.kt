package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Class whose instances represent <i>manual reset events</i>.
 *
 * This synchronization object has the same behavior as the one provided in Windows.
 * Also known as <i>gate</i>.
 *
 * Gate open 	<=> manual reset event signaled
 * Gate closed 	<=> manual reset event not signaled
 *
 * The implementation uses a "kernel style" solution pattern, also named "execution delegation" pattern.
 *
 * Can we do better?
 */
class ManualResetEventIntrinsic(val initialSignaledState: Boolean) : Object() {

    private var isSignaled = initialSignaledState

    private class Request(var isSignaled: Boolean = false)
    private val requests = mutableListOf<Request>()

    private fun unblockThreads() {
        while (requests.isNotEmpty())
            requests.removeFirst().isSignaled = true

        notifyAll()
    }

    /**
     * Sets the manual reset event to the signaled state. Waiting threads are unblocked.
     */
    @Synchronized
    fun set() {
        if (!isSignaled) {
            isSignaled = true
            unblockThreads()
        }
    }

    /**
     * Sets the manual reset event to the non signaled state.
     */
    @Synchronized
    fun reset() {
        isSignaled = false
    }

    /**
     * Blocks the calling thread until the event becomes signaled or the specified time as elapsed.
     *
     * @return A boolean value indicating the return reason: true, the event was signaled; false, the
     * specified time interval has elapsed.
     * @throws InterruptedException If the blocked thread has been signaled for cancellation.
     */
    @Throws(InterruptedException::class)
    @Synchronized
    fun waitOne(timeout: Long, unit: TimeUnit): Boolean {

        if (isSignaled)
            return true

        var remainingTime = unit.toMillis(timeout)
        val myRequest = Request(isSignaled = false)
        requests.add(myRequest)

        while (true) {
            try {
                wait(remainingTime)
            }
            catch (ie: InterruptedException) {
                requests.remove(myRequest)
                throw ie
            }

            if (myRequest.isSignaled)
                return true

            if (remainingTime <= 0) {
                requests.remove(myRequest)
                return false
            }
        }
    }
}

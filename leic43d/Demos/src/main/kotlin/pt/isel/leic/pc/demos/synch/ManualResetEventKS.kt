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
class ManualResetEventKS(val initialSignaledState: Boolean) {

    private var isSignaled = initialSignaledState

    private class Request(var isSignaled: Boolean = false)
    private val requests = mutableListOf<Request>()

    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    private fun unblockThreads() {
        while (requests.isNotEmpty())
            requests.removeFirst().isSignaled = true
        mCondition.signalAll()
    }

    /**
     * Sets the manual reset event to the signaled state. Waiting threads are unblocked.
     */
    fun set() {
        mLock.withLock {
            if (!isSignaled) {
                isSignaled = true
                unblockThreads()
            }
        }
    }

    /**
     * Sets the manual reset event to the non signaled state.
     */
    fun reset() {
        mLock.withLock {
            isSignaled = false
        }
    }

    /**
     * Blocks the calling thread until the event becomes signaled or the specified time as elapsed.
     *
     * @return A boolean value indicating the return reason: true, the event was signaled; false, the
     * specified time interval has elapsed.
     * @throws InterruptedException If the blocked thread has been signaled for cancellation.
     */
    @Throws(InterruptedException::class)
    fun waitOne(timeout: Long, unit: TimeUnit): Boolean {
        mLock.withLock {

            if (isSignaled)
                return true

            var remainingTime = unit.toNanos(timeout)
            val myRequest = Request(isSignaled = false)
            requests.add(myRequest)

            while (true) {
                try {
                    remainingTime = mCondition.awaitNanos(remainingTime)
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
}

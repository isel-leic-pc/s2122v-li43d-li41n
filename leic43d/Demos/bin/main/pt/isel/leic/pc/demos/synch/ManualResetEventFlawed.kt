package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit

/**
 * Class whose instances represent <i>manual reset events</i>.
 *
 * This synchronization object has the same behavior as the one provided in Windows.
 * Also known as <i>gate</i>.
 *
 * Gate open 	<=> manual reset event signaled
 * Gate closed 	<=> manual reset event not signaled
 *
 *  It is a flawed implementation. To understand the implementation flaw imagine the
 *  following scenario: calls to set - reset in rapid succession. What would happen?
 */
class ManualResetEventFlawed(val initialSignaledState: Boolean) {

    /**
     * Sets the manual reset event to the signaled state. Waiting threads are unblocked.
     */
    fun set() {
        TODO()
    }

    /**
     * Sets the manual reset event to the non signaled state.
     */
    fun reset() {
        TODO()
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
        TODO()
    }
}

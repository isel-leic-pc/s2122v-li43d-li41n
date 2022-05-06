package pt.isel.leic.pc.echo

import java.util.concurrent.atomic.AtomicInteger

object SessionInfo {

    private val sessionCount = AtomicInteger(0)
    private val currentCount = AtomicInteger(0)

    val totalSessions: Int
        get() = sessionCount.get()

    val currentSessions: Int
        get() = currentCount.get()

    fun createSession(): Int {
        currentCount.incrementAndGet()
        return sessionCount.incrementAndGet()
    }

    fun endSession() {
        currentCount.decrementAndGet()
    }
}

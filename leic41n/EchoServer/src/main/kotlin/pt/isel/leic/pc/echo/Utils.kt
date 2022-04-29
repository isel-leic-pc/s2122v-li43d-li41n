package pt.isel.leic.pc.echo

import java.io.BufferedWriter
import java.util.concurrent.atomic.AtomicInteger

/**
 * Extension function that prints a new line with the given string to this [BufferedWriter].
 */
fun BufferedWriter.println(str: String) {
    write(str)
    newLine()
    flush()
}

object SessionInfo {

    private val sessionCount = AtomicInteger(0)
    private val totalCount = AtomicInteger(0)

    val currentSessions: Int
        get() = sessionCount.get()

    val totalSessions: Int
        get() = totalCount.get()

    fun createSession(): Int {
        sessionCount.incrementAndGet()
        return totalCount.incrementAndGet()
    }

    fun endSession() {
        sessionCount.decrementAndGet()
    }
}

class AnotherSessionInfo {

    private val sessionCount = AtomicInteger(0)
    private val totalCount = AtomicInteger(0)

    val currentSessions: Int
        get() = sessionCount.get()

    val totalSessions: Int
        get() = totalCount.get()

    fun createSession(): Int {
        sessionCount.incrementAndGet()
        return totalCount.incrementAndGet()
    }

    fun endSession() {
        sessionCount.decrementAndGet()
    }
}

val instance = AnotherSessionInfo()


package pt.isel.leic.pc.echo.solution

import kotlinx.coroutines.CoroutineScope
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Singleton instance used to keep track of existing echo sessions. The instance is thread-safe.
 */
class SessionManager {

    /**
     * Number of client sessions initiated during the server's current execution
     */
    private val sessionCount = AtomicInteger(0)

    /**
     * The open sessions.
     */
    private val openSessions = ConcurrentHashMap<Int, Session>()

    /**
     * Creates a client session.
     * @param socket    the socket for the new client session
     * @param scope     the scope that determines the created coroutines lifespan
     */
    fun createSession(socket: AsynchronousSocketChannel, scope: CoroutineScope): Session =
        Session(sessionCount.incrementAndGet(), socket, scope).also {
            openSessions[it.id] = it
        }

    /**
     * Gets the client session identified by [id], or null if it doesn't exist
     */
    operator fun get(id: Int): Session? = openSessions[id]

    /**
     * Removes the session with the given identifier from the list of open sessions.
     */
    fun removeSession(id: Int) = openSessions.remove(id)

    /**
     * The list of current sessions.
     */
    val roaster: List<Session>
        get() = openSessions.values.toList()
}

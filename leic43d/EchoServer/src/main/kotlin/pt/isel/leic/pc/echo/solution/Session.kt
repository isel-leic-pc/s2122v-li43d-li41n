package pt.isel.leic.pc.echo.solution

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.TimeUnit

private const val EXIT = "exit"

private val logger = LoggerFactory.getLogger(Session::class.java)

class Session(
    val id: Int,
    private val socket: AsynchronousSocketChannel,
    private val scope: CoroutineScope
) {
    private enum class State { NOT_STARTED, STARTED, STOPPED }

    private val guard = Mutex()

    // Shared mutable state, guarded by [guard]
    private var state = State.NOT_STARTED
    private var stopHandler: ((Session) -> Unit)? = null

    private lateinit var rxJob: Job
    private lateinit var txJob: Job
    private val ctrlChannel = Channel<ControlMessage>()

    private suspend fun notifyOnStop() {
        guard.withLock { stopHandler }?.invoke(this)
    }

    /**
     * Starts this session mobilizing the required resources.
     * @return this session instance, for fluent use
     * @throws IllegalStateException if the session has been previously started.
     */
    suspend fun start(): Session =
        guard.withLock {
            if (state != State.NOT_STARTED)
                throw IllegalStateException("Session has already been started")

            txJob = startTxCoroutine()
            rxJob = startRxCoroutine()

            state = State.STARTED
            this
        }

    /**
     * Stops this session, releasing the associated resources. The function returns BEFORE the session's resources are
     * released.
     * @param message message to be sent to the client prior to the session termination
     * @return this session instance, for fluent use
     * @throws IllegalStateException if the session is not started.
     */
    suspend fun stop(message: String): Session {
        guard.withLock {
            if (state != State.STARTED)
                throw IllegalStateException("Session is not started")
            state = State.STOPPED
        }

        rxJob.cancel()
        ctrlChannel.send(EndSession(message))
        return this
    }

    /**
     * Starts the coroutine that is responsible for receiving messages from the client
     * @return  the [Job] instance that represents the started coroutine
     */
    private fun startRxCoroutine(): Job =
        scope.launch {
            var echoCount = 0
            try {
                while (true) {
                    when (val line = socket.suspendingReadLine(5, TimeUnit.MINUTES)) {
                        EXIT -> {
                            logger.info("Session $id terminated by user")
                            ctrlChannel.send(EndSession("Bye!"))
                            break
                        }
                        null -> {
                            logger.info("Session $id timed out")
                            ctrlChannel.send(EndSession("Session has been idle for too long. Terminating."))
                            break
                        }
                        else -> {
                            logger.info("Received line number '${++echoCount}'. Echoing it.")
                            ctrlChannel.send(Echo("($echoCount) Echo: $line"))
                        }
                    }
                }
            }
            catch (e: IOException) {
                logger.warn("Exception caught in RX coroutine for session $id. Session will end.", e)
                txJob.cancel(CancellationException("An error occurred while reading from the session socket"))
            }
        }

    /**
     * Starts the coroutine that is responsible for controlling the session and sending messages to the client
     * @return  the [Job] instance that represents the started coroutine
     */
    private fun startTxCoroutine(): Job =
        scope.launch {
            try {
                socket.use {
                    it.suspendingWriteLine("Welcome client number $id!")
                    it.suspendingWriteLine("I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!")
                    while (true) {
                        val message = ctrlChannel.receive()
                        it.suspendingWriteLine(message.content)
                        if (message is EndSession)
                            break
                    }
                }
            }
            catch (e: Exception) {
                logger.warn("Exception caught in TX coroutine for session $id. Session will end.", e)
                rxJob.cancelAndJoin()
            }
            finally {
                notifyOnStop()
            }
        }

    /**
     * Registers the given function to be called when the session is stopped. If a registration already exists, it'll
     * be overwritten.
     * @param handler   the function called when the session is stopped
     */
    suspend fun onStop(handler: (Session) -> Unit) {
        guard.withLock {
            stopHandler = handler
        }
    }
}

/**
 * Sum type used to represent the supported control messages.
 */
private sealed class ControlMessage(val content: String)
private class Echo(message: String): ControlMessage(message)
private class EndSession(message: String): ControlMessage(message)


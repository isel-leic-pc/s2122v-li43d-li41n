package pt.isel.leic.pc.echo.solution

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import sun.tools.jconsole.Messages.EXIT
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
    private var state = State.NOT_STARTED
    private val guard = Mutex()

    private lateinit var rxJob: Job
    private lateinit var txJob: Job
    private val ctrlChannel = Channel<ControlMessage>()

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
     * @return this session instance, for fluent use
     * @throws IllegalStateException if the session is not started.
     */
    suspend fun stop() {
        TODO("Not yet implemented")
    }

    /**
     * Starts the coroutine that is responsible for receiving messages from the client
     * @return  the [Job] instance that represents the started coroutine
     */
    private fun startRxCoroutine(): Job =
        scope.launch {
            var echoCount = 0
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

    /**
     * Starts the coroutine that is responsible for controlling the session and sending messages to the client
     * @return  the [Job] instance that represents the started coroutine
     */
    private fun startTxCoroutine(): Job =
        scope.launch {
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

            // TODO: Cleanup?
        }

}

/**
 * Sum type used to represent the supported control messages.
 */
private sealed class ControlMessage(val content: String)
private class Echo(message: String): ControlMessage(message)
private class EndSession(message: String): ControlMessage(message)


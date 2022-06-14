package pt.isel.leic.pc.echo.solution

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.TimeUnit

private const val EXIT = "exit"

private val logger = LoggerFactory.getLogger(Session::class.java)

/**
 * Represents echo sessions. Echo session instances cannot be reused, that is, once started and stopped, they cannot
 * be restarted.
 *
 * Each session comprises two coroutines and a control channel used for orchestration. One coroutine ([rxJob]) is used
 * for receiving messages from the client; the other ([txJob]) is used for sending messages to the client and for
 * waiting for control messages placed in the control channel.
 */
class Session(val id: Int, private val socket: AsynchronousSocketChannel, private val scope: CoroutineScope) {

    enum class State { NOT_STARTED, STARTED, STOPPED }

    private val guard = Mutex()

    // Shared mutable state, guarded by [guard]
    private var state = State.NOT_STARTED
    private var stopHandler: ((Session) -> Unit)? = null

    // Immutable data
    private val ctrlChannel = Channel<ControlMessage>()
    private lateinit var rxJob: Job
    private lateinit var txJob: Job


    private suspend fun notifyOnStop() {
        guard.withLock { stopHandler } ?.invoke(this@Session)
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
     * @return this session instance, for fluent use
     * @throws IllegalStateException if the session is not started.
     */
    suspend fun stop(message: String): Session {
        guard.withLock {
            if (state != State.STARTED)
                throw IllegalStateException("Session is not started")
            state = State.STOPPED
        }

        ctrlChannel.send(EndSession(message))
        return this
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

    /**
     * Starts the coroutine that is responsible for receiving messages from the client. When the session ends, this
     * coroutine always ends prior to the TX coroutine, even if any errors occurred.
     * @return  the [Job] instance that represents the started coroutine
     */
    private fun startRxCoroutine(): Job =
        scope.launch {
            var echoCount = 0
            // TODO: handle errors
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
     * Starts the coroutine that is responsible for controlling the session and sending messages to the client. This
     * coroutine will always finish after RX coroutine.
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
            finally {
                rxJob.join()
                notifyOnStop()
            }
        }
}

/**
 * Sum type used to represent the supported control messages.
 */
private sealed class ControlMessage(val content: String)
private class Echo(message: String): ControlMessage(message)
private class EndSession(message: String): ControlMessage(message)


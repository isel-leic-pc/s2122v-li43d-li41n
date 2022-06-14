package pt.isel.leic.pc.echo.coroutines

import createChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import suspendingAccept
import suspendingReadLine
import suspendingWriteLine
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private const val EXIT = "exit"
private val logger = LoggerFactory.getLogger("Coroutines and Flows")

/**
 * Number of client sessions initiated during the server's current execution
 */
private val sessionCount = AtomicInteger(0)

/**
 * Creates a client session, incrementing the number of initiated sessions.
 */
private fun createSession(): Int = sessionCount.incrementAndGet()

/**
 * The server's entry point.
 */
fun main(args: Array<String>) {
    val port = if (args.isEmpty() || args[0].toIntOrNull() == null) 8000 else args[0].toInt()
    logger.info("Process id is = ${ProcessHandle.current().pid()}. Starting echo server at port $port")

    val executor = Executors.newSingleThreadExecutor()
    val serverSocket = createChannel("localhost", port, executor)

    val throttle = AsyncSemaphore(2)
    val serverLoop = CoroutineScope(executor.asCoroutineDispatcher()).launch {

        serverSocket.toFlow(throttle).collect { socket ->
            launch {
                handleEchoSession(socket)
                throttle.release()
            }
        }
    }

    // Emulates the server console
    readln()

    logger.info("Shutting down...")
    runBlocking {
        serverLoop.cancelAndJoin()
    }

    executor.shutdown()
    executor.awaitTermination(10, TimeUnit.SECONDS)
    logger.info("Done!")
}

private suspend fun handleEchoSession(sessionSocket: AsynchronousSocketChannel) {
    val sessionId = createSession()
    var echoCount = 0
    sessionSocket.use {
        sessionSocket.suspendingWriteLine("Welcome client number $sessionId!")
        sessionSocket.suspendingWriteLine("I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!")

        sessionSocket.toFlow().collect { line ->
            logger.info("Received line number '${++echoCount}'. Echoing it.")
            sessionSocket.suspendingWriteLine("($echoCount) Echo: $line")
        }

        sessionSocket.suspendingWriteLine("Bye!")
    }
}

/**
 * Extension function that produces a flow of client messages (text lines) received from this
 * [AsynchronousSocketChannel] instance.
 */
suspend fun AsynchronousSocketChannel.toFlow(): Flow<String> =
    flow {
        while (true) {
            val line = suspendingReadLine()
            if (line == null || line == EXIT)
                break
            emit(line)
        }
    }

/**
 * Extension function that produces a flow of [AsynchronousSocketChannel] client connections from this
 * [AsynchronousServerSocketChannel] instance.
 */
suspend fun AsynchronousServerSocketChannel.toFlow(throttle: AsyncSemaphore): Flow<AsynchronousSocketChannel> =
    flow {
        try {
            while(true) {
                throttle.acquire().await()
                logger.info("Ready to accept connections")
                val sessionSocket = suspendingAccept()
                emit(sessionSocket)
            }
        }
        finally {
            logger.info("Flow of connections ends")
        }
    }

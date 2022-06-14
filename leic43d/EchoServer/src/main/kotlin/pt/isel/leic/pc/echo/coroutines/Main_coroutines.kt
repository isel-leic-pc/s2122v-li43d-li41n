package pt.isel.leic.pc.echo.coroutines

import createChannel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import suspendingAccept
import suspendingReadLine
import suspendingWriteLine
import java.nio.channels.AsynchronousSocketChannel
import java.util.LinkedList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val EXIT = "exit"
private val logger = LoggerFactory.getLogger("Coroutines and NIO")

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
        while (true) {
            throttle.acquire().await()
            logger.info("Ready to accept connections")
            val socket = serverSocket.suspendingAccept()
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
        try {
            sessionSocket.suspendingWriteLine("Welcome client number $sessionId!")
            sessionSocket.suspendingWriteLine("I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!")
            while (true) {
                when (val line = sessionSocket.suspendingReadLine(5, TimeUnit.MINUTES)) {
                    null -> {
                        sessionSocket.suspendingWriteLine("Session closed for inactivity")
                        break
                    }
                    EXIT -> {
                        sessionSocket.suspendingWriteLine("Bye!")
                        break
                    }
                    else -> {
                        logger.info("Received line number '${++echoCount}'. Echoing it.")
                        sessionSocket.suspendingWriteLine("($echoCount) Echo: $line")
                    }
                }
            }
        }
        catch (cancelled: CancellationException) {
            logger.info("I was cancelled.")
            withContext(NonCancellable) {
                logger.info("Saying goodbye.")
                sessionSocket.suspendingWriteLine("The server is shutting down. Please come back later.")
            }
        }
    }
}

class AsyncSemaphore(initialUnits: Int) {

    private var units: Int = initialUnits
    private val guard = ReentrantLock()
    private val requests = LinkedList<CompletableFuture<Unit>>()

    fun release() {
        guard.withLock {
            if (requests.isEmpty()) {
                units += 1
                null
            }
            else {
                requests.removeFirst()
            }
        }?.complete(Unit)
    }

    fun acquire(): CompletableFuture<Unit> {
        guard.withLock {
            if (units != 0) {
                units -= 1
                return CompletableFuture.completedFuture(Unit)
            }

            val operation = CompletableFuture<Unit>()
            requests.addLast(operation)
            return operation
        }
    }
}

suspend fun <T> CompletableFuture<T>.await(): T =
    suspendCoroutine { continuation ->
        this.whenComplete { result, error ->
            if (error != null)
                continuation.resumeWithException(error)
            else
                continuation.resume(result)
        }
    }

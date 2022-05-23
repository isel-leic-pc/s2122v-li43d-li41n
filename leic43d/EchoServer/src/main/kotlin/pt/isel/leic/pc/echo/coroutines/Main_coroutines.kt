package pt.isel.leic.pc.echo.coroutines

import createChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import pt.isel.leic.pc.echo.SessionInfo
import pt.isel.leic.pc.echo.println
import suspendingAccept
import suspendingReadLine
import suspendingWriteLine
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.AsynchronousChannelGroup
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
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
    val scope = CoroutineScope(executor.asCoroutineDispatcher())
    scope.launch {
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

    readln()

    logger.info("Shutting down...")
    scope.cancel()

    executor.shutdown()
    executor.awaitTermination(10, TimeUnit.SECONDS)
    logger.info("Done!")
}


suspend fun handleEchoSession(sessionSocket: AsynchronousSocketChannel) {
    val sessionId = createSession()
    var echoCount = 0
    sessionSocket.use {
        sessionSocket.suspendingWriteLine("Welcome client number $sessionId!")
        sessionSocket.suspendingWriteLine("I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!")
        while (true) {
            val line = sessionSocket.suspendingReadLine()
            if (line == EXIT)
                break
            logger.info("Received line number '${++echoCount}'. Echoing it.")
            sessionSocket.suspendingWriteLine("($echoCount) Echo: $line")
        }
        sessionSocket.suspendingWriteLine("Bye!")
    }
}

private class AsyncSemaphore(initialUnits: Int) {

    private var units: Int = initialUnits
    private val guard = ReentrantLock()
    private val requests = LinkedList<CompletableFuture<Unit>>()

    fun release() {
        guard.withLock {
            if (requests.isEmpty()) {
                units += 1
                return
            }
            requests.removeFirst().complete(Unit)
        }
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

package pt.isel.leic.pc.echo.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.slf4j.LoggerFactory
import pt.isel.leic.pc.echo.SessionInfo
import java.nio.channels.AsynchronousSocketChannel
import java.util.LinkedList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val EXIT = "exit"
private val logger = LoggerFactory.getLogger("Async IO NIO Echo Server")

private class AsyncSemaphore(initialUnits: Int) {

    private var units: Int = initialUnits
    private val guard = ReentrantLock()
    private val requests = LinkedList<CompletableFuture<Unit>>()

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

    fun release() {
        guard.withLock {
            if (requests.isEmpty()) {
                units += 1
                null
            } else {
                requests.removeFirst()
            }
        }?.complete(Unit)
    }
}


/**
 * The server's entry point.
 */
fun main(args: Array<String>) {
    val port = if (args.isEmpty() || args[0].toIntOrNull() == null) 8000 else args[0].toInt()

    logger.info("Process id is = ${ProcessHandle.current().pid()}. Starting echo server at port $port")

    val executor = Executors.newSingleThreadExecutor()
    val serverSocket = createServerChannel("localhost", port, executor)

    val throttle = AsyncSemaphore(2)

    CoroutineScope(executor.asCoroutineDispatcher()).launch {
        while (true) {
            throttle.acquire().await()
            logger.info("Ready to accept connections")
            val sessionSocket = serverSocket.suspendingAccept()
            launch {
                try {
                    handleEchoSession(sessionSocket)
                }
                finally {
                    throttle.release()
                }
            }
        }
    }

    logger.info("Blocking on readln")
    readln()

    // Initiate shutdown ...


}

suspend fun <T> CompletableFuture<T>.await(): T {
    return suspendCoroutine { continuation ->
        this.whenComplete { result, error ->
            if (error != null)
                continuation.resumeWithException(error)
            else
                continuation.resume(result)
        }
    }
}

suspend fun handleEchoSession(sessionSocket: AsynchronousSocketChannel) {
    val sessionId = SessionInfo.createSession()
    var echoCount = 0
    sessionSocket.use {
        logger.info("Accepted session $sessionId")
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
        SessionInfo.endSession()
    }
}

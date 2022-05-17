package pt.isel.leic.pc.echo

import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.AsynchronousChannelGroup
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.LinkedList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val EXIT = "exit"
private val logger = LoggerFactory.getLogger("Async IO NIO Echo Server")

private val encoder = Charsets.UTF_8.newEncoder()
private val decoder = Charsets.UTF_8.newDecoder()

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
            } else {
                requests.removeFirst().complete(Unit)
            }
        }
    }
}


/**
 * The server's entry point.
 */
fun main(args: Array<String>) {
    val port = if (args.isEmpty() || args[0].toIntOrNull() == null) 8000 else args[0].toInt()

    val group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor())
    val serverSocket = AsynchronousServerSocketChannel.open(group)
    serverSocket.bind(InetSocketAddress("localhost", port))

    logger.info("Process id is = ${ProcessHandle.current().pid()}. Starting echo server at port $port")
    val throttle = AsyncSemaphore(2)

    fun acceptSession(serverSocket: AsynchronousServerSocketChannel) {
        throttle.acquire().thenRun {
            serverSocket.accept(null, object : CompletionHandler<AsynchronousSocketChannel, Any?> {
                override fun completed(sessionSocket: AsynchronousSocketChannel, attachment: Any?) {
                    greet(sessionSocket, throttle) {
                        handleEchoes(sessionSocket, throttle) {
                            goodbye(sessionSocket, throttle)
                        }
                    }
                    acceptSession(serverSocket)
                }

                override fun failed(exc: Throwable?, attachment: Any?) {
                    logger.error("Failed to accept.")
                }
            })
        }
    }

    acceptSession(serverSocket)
    readln()
    // Code to handle server termination goes not
}

private fun greet(sessionSocket: AsynchronousSocketChannel, throttle: AsyncSemaphore, andThen: (AsynchronousSocketChannel) -> Unit) {
    val sessionId = SessionInfo.createSession()

    val greeting = CharBuffer.wrap("Welcome client number $sessionId!\n" +
            "I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!\n")

    sessionSocket.write(encoder.encode(greeting), null, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int, attachment: Any?) {
            logger.info("Sent greeting to the client")
            andThen(sessionSocket)
        }

        override fun failed(exc: Throwable, attachment: Any?) {
            logger.error("Failed to send greeting to the client", exc)
            sessionCleanup(sessionSocket, throttle)
        }
    })

}

private fun handleEchoes(sessionSocket: AsynchronousSocketChannel, throttle: AsyncSemaphore, andThen: (AsynchronousSocketChannel) -> Unit) {
    val buffer = ByteBuffer.allocate(1024)
    handleEcho(buffer, sessionSocket, 1, throttle, andThen)
}

private fun handleEcho(
    buffer: ByteBuffer,
    sessionSocket: AsynchronousSocketChannel,
    echoCount: Int,
    throttle: AsyncSemaphore,
    andThen: (AsynchronousSocketChannel) -> Unit
) {
    sessionSocket.read(buffer, null, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int?, attachment: Any?) {
            val clientMessage = decoder.decode(buffer.flip()).toString().trim()

            if (clientMessage == EXIT)
                andThen(sessionSocket)
            else {
                val echo = "($echoCount) Echo: $clientMessage\n"
                sessionSocket.write(encoder.encode(CharBuffer.wrap(echo)), null, object : CompletionHandler<Int, Any?> {
                    override fun completed(result: Int?, attachment: Any?) {
                        buffer.clear()
                        handleEcho(buffer, sessionSocket, echoCount + 1, throttle, andThen)
                    }

                    override fun failed(exc: Throwable?, attachment: Any?) {
                        logger.error("Failed to echo message to the client", exc)
                        sessionCleanup(sessionSocket, throttle)
                    }
                })
            }
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            logger.error("Failed to receive message from client", exc)
            sessionCleanup(sessionSocket, throttle)
        }

    })


}

private fun goodbye(sessionSocket: AsynchronousSocketChannel, throttle: AsyncSemaphore) {
    sessionSocket.write(encoder.encode(CharBuffer.wrap("Bye!\n")), null, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int?, attachment: Any?) {
            sessionCleanup(sessionSocket, throttle)
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            logger.error("Failed to say goodbye );", exc)
            sessionCleanup(sessionSocket, throttle)
        }
    })
}

private fun sessionCleanup(sessionSocket: AsynchronousSocketChannel, throttle: AsyncSemaphore) {
    throttle.release()
    sessionSocket.close()
    SessionInfo.endSession()
}

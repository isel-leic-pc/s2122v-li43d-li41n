package pt.isel.leic.pc.echo

import org.slf4j.LoggerFactory
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
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val EXIT = "exit"
private val logger = LoggerFactory.getLogger("Async IO with NIO")

private val encoder = Charsets.UTF_8.newEncoder()
private val decoder = Charsets.UTF_8.newDecoder()

private class AsyncSemaphore(initialUnits: Int) {

    private var units: Int = initialUnits
    private val guard = ReentrantLock()
    private val requests = LinkedList<CompletableFuture<Unit>>()

    fun release() {
        guard.withLock {
            if (requests.isEmpty()) { units += 1; null }
            else { requests.removeFirst() }
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

    fun acceptConnection() {
        throttle.acquire().thenRun {
            serverSocket.accept(null, object : CompletionHandler<AsynchronousSocketChannel, Any?> {
                override fun completed(socket: AsynchronousSocketChannel, attachment: Any?) {
                    with(socket) {
                        acceptConnection()
                        greet(SessionInfo.createSession(), throttle) {
                            handleEchoSession(throttle) {
                                sayGoodBye(throttle) {
                                    sessionCleanup(throttle)
                                }
                            }
                        }
                    }
                }

                override fun failed(exc: Throwable?, attachment: Any?) {
                    logger.error("Failed to accept client connection", exc)
                }
            })
        }
    }

    acceptConnection()
    readln()
    // Server cleanup comes here ...
}

private fun AsynchronousSocketChannel.handleEchoSession(throttle: AsyncSemaphore, andThen: (AsynchronousSocketChannel) -> Unit) {
    val buffer = ByteBuffer.allocate(1024)
    handleEchoSessionImpl(buffer, 1, throttle, andThen)
}

private fun AsynchronousSocketChannel.handleEchoSessionImpl(buffer: ByteBuffer, echoCount: Int, throttle: AsyncSemaphore, andThen: (AsynchronousSocketChannel) -> Unit) {

    read(buffer, null, object: CompletionHandler<Int, Any?> {
        override fun completed(result: Int, attachment: Any?) {
            val clientMessage = decoder.decode(buffer.flip()).toString().trim()
            if (clientMessage == EXIT) {
                andThen(this@handleEchoSessionImpl)
            }
            else {
                val echo = encoder.encode(CharBuffer.wrap("(${echoCount}) Echo: $clientMessage\n"))
                write(echo, null, object : CompletionHandler<Int, Any?> {
                    override fun completed(result: Int?, attachment: Any?) {
                        buffer.clear()
                        handleEchoSessionImpl(buffer, echoCount + 1, throttle, andThen)
                    }

                    override fun failed(exc: Throwable?, attachment: Any?) {
                        logger.error("Failed to echo message to the client socket", exc)
                        sessionCleanup(throttle)
                    }
                })
            }
        }

        override fun failed(exc: Throwable, attachment: Any?) {
            logger.error("Failed to receive message from client socket", exc)
            sessionCleanup(throttle)
        }
    })
}


private fun AsynchronousSocketChannel.greet(sessionId: Int, throttle: AsyncSemaphore, andThen: (AsynchronousSocketChannel) -> Unit) {
    val msg = CharBuffer.wrap("Welcome client number $sessionId!" +
            "I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!\n")

    write(encoder.encode(msg), null, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int, attachment: Any?) {
            logger.info("Sent greeting to client.")
            andThen(this@greet)
        }
        override fun failed(exc: Throwable, attachment: Any?) {
            logger.error("Failed to write to client socket", exc)
            sessionCleanup(throttle)
        }
    })
}

private fun AsynchronousSocketChannel.sayGoodBye(throttle: AsyncSemaphore, andThen: (AsynchronousSocketChannel) -> Unit) {
    write(encoder.encode(CharBuffer.wrap("Bye!\n")), null, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int?, attachment: Any?) {
            andThen(this@sayGoodBye)
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            logger.error("Failed to write to client socket", exc)
            sessionCleanup(throttle)
        }
    })
}

private fun AsynchronousSocketChannel.sessionCleanup(throttle: AsyncSemaphore) {
    throttle.release()
    close()
    SessionInfo.endSession()
}
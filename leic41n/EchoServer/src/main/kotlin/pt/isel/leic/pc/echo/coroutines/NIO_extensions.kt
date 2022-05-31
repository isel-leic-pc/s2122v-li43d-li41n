package pt.isel.leic.pc.echo.coroutines

import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.AsynchronousChannelGroup
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.nio.charset.CharsetDecoder
import java.nio.charset.CharsetEncoder
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private val logger = LoggerFactory.getLogger("NIO extensions")
private val encoder: CharsetEncoder = Charsets.UTF_8.newEncoder()
private val decoder: CharsetDecoder = Charsets.UTF_8.newDecoder()


fun createServerChannel(hostname: String, port: Int, executor: ExecutorService): AsynchronousServerSocketChannel {
    val group = AsynchronousChannelGroup.withThreadPool(executor)
    val serverSocket = AsynchronousServerSocketChannel.open(group)
    serverSocket.bind(InetSocketAddress(hostname, port))
    return serverSocket
}

suspend fun AsynchronousServerSocketChannel.suspendingAccept(): AsynchronousSocketChannel {
    return suspendCoroutine { continuation ->
        accept(null, object : CompletionHandler<AsynchronousSocketChannel, Any?> {
            override fun completed(sessionSocket: AsynchronousSocketChannel, attachment: Any?) {
                logger.info("Accept succeeded.")
                continuation.resume(sessionSocket)
            }

            override fun failed(error: Throwable, attachment: Any?) {
                logger.error("Failed to accept.", error)
                continuation.resumeWithException(error)
            }
        })
    }
}

suspend fun AsynchronousSocketChannel.suspendingWriteLine(text: String): Int {
    return suspendCoroutine { continuation ->
        val toSend = CharBuffer.wrap(text + "\r\n")
        // This is NOT production ready! It's a DEMO!
        // E.g. We would need to deal with the case when not all the string's chars are written in one call
        write(encoder.encode(toSend), null, object : CompletionHandler<Int, Any?> {
            override fun completed(result: Int, attachment: Any?) {
                logger.info("Write succeeded.")
                continuation.resume(result)
            }

            override fun failed(error: Throwable, attachment: Any?) {
                logger.error("Write failed.")
                continuation.resumeWithException(error)
            }
        })
    }
}

suspend fun AsynchronousSocketChannel.suspendingReadLine(): String {
    return suspendCoroutine { continuation ->
        val buffer = ByteBuffer.allocate(1024)
        // This is NOT production ready! It's a DEMO!
        // E.g. We would need to deal with the case when read does not contain the whole line
        read(buffer, null, object : CompletionHandler<Int, Any?> {
            override fun completed(result: Int, attachment: Any?) {
                logger.info("Read succeeded.")
                val received = decoder.decode(buffer.flip()).toString().trim()
                continuation.resume(received)
            }

            override fun failed(error: Throwable, attachment: Any?) {
                logger.error("Read failed.")
                continuation.resumeWithException(error)
            }
        })
    }
}
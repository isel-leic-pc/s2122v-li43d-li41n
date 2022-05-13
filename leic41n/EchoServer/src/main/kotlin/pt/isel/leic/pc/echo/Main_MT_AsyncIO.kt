package pt.isel.leic.pc.echo

import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler

private const val EXIT = "exit"
private val logger = LoggerFactory.getLogger("Async IO NIO Echo Server")

val encoder = Charsets.UTF_8.newEncoder()
val decoder = Charsets.UTF_8.newDecoder()

/**
 * The server's entry point.
 */
fun main(args: Array<String>) {
    val port = if (args.isEmpty() || args[0].toIntOrNull() == null) 8000 else args[0].toInt()
    val serverSocket = AsynchronousServerSocketChannel.open()
    serverSocket.bind(InetSocketAddress("localhost", port))

    logger.info("Process id is = ${ProcessHandle.current().pid()}. Starting echo server at port $port")
    while (true) {
        val sessionSocket = serverSocket.accept().get()

        greet(sessionSocket) {
            handleEchoes(sessionSocket) {
                goodbye(sessionSocket)
            }
        }
    }
}

private fun greet(sessionSocket: AsynchronousSocketChannel, andThen: (AsynchronousSocketChannel) -> Unit) {
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
            sessionSocket.close()
            SessionInfo.endSession()
        }
    })

}

private fun handleEchoes(sessionSocket: AsynchronousSocketChannel, andThen: (AsynchronousSocketChannel) -> Unit) {
    val buffer = ByteBuffer.allocate(1024)
    handleEcho(buffer, sessionSocket, 1, andThen)
}

private fun handleEcho(
    buffer: ByteBuffer,
    sessionSocket: AsynchronousSocketChannel,
    echoCount: Int,
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
                        handleEcho(buffer, sessionSocket, echoCount + 1, andThen)
                    }

                    override fun failed(exc: Throwable?, attachment: Any?) {
                        logger.error("Failed to echo message to the client", exc)
                        sessionCleanup(sessionSocket)
                    }
                })
            }
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            logger.error("Failed to receive message from client", exc)
            sessionCleanup(sessionSocket)
        }

    })


}

private fun goodbye(sessionSocket: AsynchronousSocketChannel) {
    sessionSocket.write(encoder.encode(CharBuffer.wrap("Bye!\n")), null, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int?, attachment: Any?) {
            sessionSocket.close()
            SessionInfo.endSession()
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            logger.error("Failed to say goodbye );", exc)
            sessionCleanup(sessionSocket)
        }
    })
}

private fun sessionCleanup(sessionSocket: AsynchronousSocketChannel) {
    sessionSocket.close()
    SessionInfo.endSession()
}

/**
 * Serves the client connected to the given [Socket] instance
 */
//private fun handleEchoSession(sessionSocket: Socket) {
//    val sessionId = SessionInfo.createSession()
//    var echoCount = 0
//    sessionSocket.use {
//        val input = BufferedReader(InputStreamReader(sessionSocket.getInputStream()))
//        val output = BufferedWriter(OutputStreamWriter(sessionSocket.getOutputStream()))
//        output.println("Welcome client number $sessionId!")
//        output.println("I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!")
//        while (true) {
//            val line = input.readLine() ?: EXIT
//            if (line == EXIT)
//                break
//            logger.info("Received line number '${++echoCount}'. Echoing it.")
//            output.println("($echoCount) Echo: $line")
//        }
//        output.println("Bye!")
//        SessionInfo.endSession()
//    }
//}

package pt.isel.leic.pc.echo

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket

private const val EXIT = "exit"
private val logger = LoggerFactory.getLogger("MultiThreaded With Thread Pool Echo Server")

/**
 * The server's entry point.
 */
fun main(args: Array<String>) {
    val port = if (args.isEmpty() || args[0].toIntOrNull() == null) 8000 else args[0].toInt()
    val serverSocket = ServerSocket(port)
    logger.info("Process id is = ${ProcessHandle.current().pid()}. Starting echo server at port $port")
    val pool = ThreadPool(6)
    while (true) {
        logger.info("Ready to accept connections")
        val sessionSocket = serverSocket.accept()
        if (pool.queueSize == 100) {
            sessionSocket.getOutputStream().write("Sorry!\n".toByteArray())
            sessionSocket.close()
        } else {
            logger.info("Accepted client connection. Sessions = ${SessionInfo.currentSessions}. " +
                    "Queue size = ${pool.queueSize}")
            pool.execute(work = {
                handleEchoSession(sessionSocket)
            })
        }
    }
}

/**
 * Serves the client connected to the given [Socket] instance
 */
private fun handleEchoSession(sessionSocket: Socket) {
    val sessionId = SessionInfo.createSession()
    var echoCount = 0
    sessionSocket.use {
        val input = BufferedReader(InputStreamReader(sessionSocket.getInputStream()))
        val output = BufferedWriter(OutputStreamWriter(sessionSocket.getOutputStream()))
        output.println("Welcome client number $sessionId!")
        output.println("I'll echo everything you send me. Finish with '$EXIT'. Ready when you are!")
        while (true) {
            val line = input.readLine() ?: EXIT
            if (line == EXIT)
                break
            logger.info("Received line number '${++echoCount}'. Echoing it.")
            output.println("($echoCount) Echo: $line")
        }
        output.println("Bye!")
        SessionInfo.endSession()
    }
}

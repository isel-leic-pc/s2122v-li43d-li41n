package pt.isel.leic.pc.echo.solution

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

private val logger = LoggerFactory.getLogger("Echo Server FTW")

/**
 * The server's entry point.
 */
fun main(args: Array<String>) {

    runBlocking {
        val port = if (args.isEmpty() || args[0].toIntOrNull() == null) 8000 else args[0].toInt()
        logger.info("Process id is = ${ProcessHandle.current().pid()}. Starting echo server at port $port")

        val server = Server(2)
        server.start(InetSocketAddress("localhost", port))

        // The server console loop would be placed here. We are merely emulating it. =)
        readln()

        logger.info("Initiating shutting down...")
        server.shutdownAndJoin("Server is shutting down for scheduled maintenance. Please come back later. =)")
    }

    logger.info("Server has shutdown")
}

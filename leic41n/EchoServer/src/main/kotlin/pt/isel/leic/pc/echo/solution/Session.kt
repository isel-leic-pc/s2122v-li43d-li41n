package pt.isel.leic.pc.echo.solution

import kotlinx.coroutines.CoroutineScope
import java.nio.channels.AsynchronousSocketChannel

/**
 * Represents echo sessions. Echo session instances cannot be reused, that is, once started and stopped, they cannot
 * be restarted.
 *
 * Each session comprises two coroutines and a control channel used for orchestration. One coroutine ([rxJob]) is used
 * for receiving messages from the client; the other ([txJob]) is used for sending messages to the client and for
 * waiting for control messages placed in the control channel.
 */
class Session(val id: Int, private val socket: AsynchronousSocketChannel, private val scope: CoroutineScope) {
    fun start() {
        TODO("Not yet implemented")
    }

    // TODO
}
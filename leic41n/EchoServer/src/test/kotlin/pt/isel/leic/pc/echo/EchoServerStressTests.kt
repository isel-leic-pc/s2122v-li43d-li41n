package pt.isel.leic.pc.echo

import java.net.Socket
import kotlin.test.Test

class EchoServerStressTests {

    @Test
    fun `open sessions until it blows`() {
        val port = 8000
        while (true) {
            val socket = Socket("localhost", port)
            socket.getOutputStream().write("Hi!\n".toByteArray())
        }
    }
}
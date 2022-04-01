package pt.isel.leic.pc.demos.synch

import org.junit.jupiter.api.assertThrows
import pt.isel.leic.pc.demos.synch.AFuture
import pt.isel.leic.pc.demos.synch.AFutureImpl
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AFutureImplTests {

    private val successText = "Yeah"
    private val error = IOException()

    /**
     * An example of an asynchronous operation. This one ´allegedly´ fetches some text from a remote server (I/O bound).
     * The function can either fail or succeed, according to [simulateFailure].
     */
    private fun fetchSomeText(simulateFailure: Boolean): AFuture<String> {
        val result = AFutureImpl<String>()
        Thread {
            Thread.sleep(2000)
            if (simulateFailure)
                result.setFailure(error)
            else
                result.setSuccess(successText)
        }.start()
        return result
    }

    @Test
    fun `get blocks until success result is available`() {
        val futureText = fetchSomeText(simulateFailure = false)
        // Do something else in the meantime
        assertEquals(successText, futureText.get())
    }

    @Test
    fun `get blocks until error result is available`() {
        assertThrows<IOException> {
            val futureText = fetchSomeText(simulateFailure = true)
            futureText.get()
        }
    }

    @Test
    fun `get blocks until timeout expires`() {
        val futureText = fetchSomeText(simulateFailure = false)
        assertNull(futureText.get(1, TimeUnit.SECONDS))
    }
}


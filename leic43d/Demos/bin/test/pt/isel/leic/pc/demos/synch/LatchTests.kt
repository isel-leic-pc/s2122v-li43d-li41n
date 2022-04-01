package pt.isel.leic.pc.demos.synch

import org.junit.jupiter.api.assertThrows
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LatchTests {

    @Test
    fun `await blocks until signaled`() {
        val completed = Latch()
        Thread {
            Thread.sleep(1000)
            completed.signal()
        }.start()
        completed.await()
    }

    @Test
    fun `await blocks until timeout`() {
        assertFalse {
            val sut = Latch()
            sut.await(2, TimeUnit.SECONDS)
        }
    }

    @Test
    fun `await blocks until interrupted`() {
        val completed = Latch()
        val toBeCancelled = Thread {
            val sut = Latch()
            assertThrows<InterruptedException> {
                sut.await(5, TimeUnit.SECONDS)
            }
            completed.signal()
        }.apply { start() }
        Thread.sleep(1000)
        toBeCancelled.interrupt()
        assertTrue { completed.await(10, TimeUnit.SECONDS) }
    }
}
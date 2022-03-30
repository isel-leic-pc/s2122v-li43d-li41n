package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFalse

class LatchTests {

    @Test
    fun `await on non signalled latch blocks until signalled`(){
        val latch = Latch()
        Thread {
            Thread.sleep(1000)
            latch.signal()
        }.start()
        latch.await()
    }

    @Test
    fun `await on non signalled latch blocks until timeout ellapses`(){
        val latch = Latch()
        Thread {
            Thread.sleep(1000)
        }.start()
        val signalled = latch.await(2, TimeUnit.SECONDS)
        assertFalse(signalled)
    }
}
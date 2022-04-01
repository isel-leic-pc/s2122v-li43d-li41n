package pt.isel.leic.pc.demos.synch

import palbp.laboratory.demos.synch.HandlerThread
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertTrue

class HandlerThreadTests {

    @Test
    fun handlerThreadExecutesActions() {
        val handler = HandlerThread(capacity = 10).apply { start() }

        val count = 5
        val countDownLatch = CountDownLatch(count)

        repeat(count) {
            handler.post(action = {
                Thread.sleep(500)
                countDownLatch.countDown()
            })
        }

        assertTrue(countDownLatch.await(10, TimeUnit.SECONDS))
    }
}
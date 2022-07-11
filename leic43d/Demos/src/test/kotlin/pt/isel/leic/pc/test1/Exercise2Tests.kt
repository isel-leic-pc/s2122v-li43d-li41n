package pt.isel.leic.pc.test1

import kotlin.test.Test
import java.util.concurrent.CountDownLatch
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.seconds

class Exercise2Tests {

    @Test
    fun `blocked threads are serviced in a FIFO manner`() {

        val messagesToProduce = 15
        val queue = MessageQueue<Int>()

        var consumer1Result: List<Int>? = null
        val consumer1MessageCount = messagesToProduce * 2 / 3
        var consumer2Result: List<Int>? = null
        val consumer2MessageCount = messagesToProduce / 3

        val isFirstWaiting = CountDownLatch(1)
        val consumer1 = Thread {
            isFirstWaiting.countDown()
            consumer1Result = queue.tryDequeue(consumer1MessageCount, timeout = 10.seconds)
        }.apply { start() }
        isFirstWaiting.await()

        val isSecondWaiting = CountDownLatch(1)
        val consumer2 = Thread {
            isSecondWaiting.countDown()
            consumer2Result = queue.tryDequeue(consumer2MessageCount, timeout = 10.seconds)
        }.apply { start() }
        isSecondWaiting.await()

        repeat(messagesToProduce) {
            queue.enqueue(it)
        }

        consumer1.join(10_000)
        consumer2.join(10_000)

        assertNotNull(consumer1Result)
        assertNotNull(consumer2Result)

        assertEquals(consumer1MessageCount, consumer1Result?.size)
        assertEquals(consumer2MessageCount, consumer2Result?.size)
        assertEquals(messagesToProduce, (consumer1Result?.size ?: 0) + (consumer2Result?.size ?: 0))

        assertContentEquals(consumer1Result?.toIntArray(), IntArray(consumer1MessageCount) { it })
        assertContentEquals(consumer2Result?.toIntArray(), IntArray(consumer2MessageCount) { it + consumer1MessageCount })
    }

    @Test
    fun `interrupting first waiting thread enables servicing of the next`() {

        val messagesToProduce = 5
        val queue = MessageQueue<Int>()
        var consumerResult: List<Int>? = null

        val isFirstWaiting = CountDownLatch(1)
        val consumerToCancel = Thread {
            isFirstWaiting.countDown()
            queue.tryDequeue(messagesToProduce * 2, timeout = 10.seconds)
        }.apply { start() }
        isFirstWaiting.await()

        val isSecondWaiting = CountDownLatch(1)
        val consumer = Thread {
            isSecondWaiting.countDown()
            consumerResult = queue.tryDequeue(messagesToProduce, timeout = 10.seconds)
        }.apply { start() }
        isSecondWaiting.await()

        repeat(messagesToProduce) {
            queue.enqueue(it)
        }

        consumerToCancel.interrupt()
        consumer.join(10_000)

        assertNotNull(consumerResult)
        assertEquals(messagesToProduce, consumerResult?.size)
        assertContentEquals(consumerResult?.toIntArray(), IntArray(messagesToProduce) { it })
    }
}
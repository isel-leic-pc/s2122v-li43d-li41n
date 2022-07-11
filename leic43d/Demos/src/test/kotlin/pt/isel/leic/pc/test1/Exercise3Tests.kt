package pt.isel.leic.pc.test1

import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Exercise3Tests {

    private fun combineResults(results: MutableList<Map<String, Int>>): Map<String, Int> {
        val allKeys = results.fold(emptySet<String>()) { acc, map -> acc + map.keys }
        return buildMap {
            allKeys.forEach { key ->
                val count = results.fold(0) { acc, map -> acc + map.getOrDefault(key, 0) }
                put(key, count)
            }
        }
    }

    private fun consumerWork(container: Container<String>): Map<String, Int> {
        Thread.sleep(1000)
        val consumedValues = mutableMapOf<String, Int>()
        while (true) {
            val consumedValue = container.consume() ?: break
            consumedValues.compute(consumedValue) { _, value ->
                if (value == null) 1 else value + 1
            }
            Thread.yield()
        }
        return consumedValues
    }

    @Test
    fun `concurrent consumes do not produce phantom results nor loss of values`() {

        val threadCount = 4
        val workers = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        val iselKey = "isel"; val iselCount = 30_000
        val pcKey = "pc"; val pcCount = 45_000
        val testKey = "test"; val testCount = 20_000
        val container = Container(
            arrayOf(
                Value(iselKey, iselCount),
                Value(pcKey, pcCount),
                Value(testKey, testCount)
            )
        )

        val partialResults = MutableList<Map<String, Int>>(threadCount) { emptyMap() }
        repeat(threadCount) {
            workers.execute { partialResults[it] = consumerWork(container); latch.countDown() }
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS))
        val results = combineResults(partialResults)

        assertEquals(3, results.size)
        assertEquals(iselCount, results[iselKey])
        assertEquals(pcCount, results[pcKey])
        assertEquals(testCount, results[testKey])
    }
}
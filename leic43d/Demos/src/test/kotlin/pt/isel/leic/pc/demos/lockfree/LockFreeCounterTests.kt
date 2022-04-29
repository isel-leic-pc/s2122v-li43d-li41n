package pt.isel.leic.pc.demos.lockfree

import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertEquals

// Number of threads used on each test
private const val NUMBER_OF_THREADS = 6

// Number of repetitions performed by each thread
private const val NUMBER_OF_REPS = 100_000

class LockFreeCounterTests {

    @Test
    fun `multiple threads incrementing a shared counter`() {

        val sharedCounter = LockFreeCounter(0)
        (0 until NUMBER_OF_THREADS).map {
            Thread {
                repeat(NUMBER_OF_REPS) {
                    sharedCounter.increment()
                }
            }.apply(Thread::start)
        }.forEach { it.join() }

        assertEquals(NUMBER_OF_REPS * NUMBER_OF_THREADS, sharedCounter.value)
    }

    @Test
    fun `increment produces the original value plus one`() {
        val allCounterValues = ConcurrentHashMap<Int, Boolean>()
        val sharedCounter = LockFreeCounter(0)
        (0 until NUMBER_OF_THREADS).map {
            Thread {
                val counterValues = HashSet<Int>()
                repeat(NUMBER_OF_REPS) {
                    counterValues.add(sharedCounter.increment())
                }
                counterValues.forEach {
                    allCounterValues[it] = true
                }
            }.apply(Thread::start)
        }.forEach(Thread::join)

        assertEquals(expected = NUMBER_OF_REPS * NUMBER_OF_THREADS, actual = allCounterValues.size)
    }

}
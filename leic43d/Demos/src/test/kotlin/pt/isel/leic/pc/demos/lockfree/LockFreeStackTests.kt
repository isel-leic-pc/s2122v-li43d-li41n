package pt.isel.leic.pc.demos.lockfree

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

// Number of threads used on each test
private const val NUMBER_OF_THREADS = 6

// Number of repetitions performed by each thread
private const val NUMBER_OF_REPS = 100_000

class LockFreeStackTests {

    @Test
    fun `multiple threads inserting values to a shared list`() {

        val sharedStack = LockFreeStack<Int>()

        (0 until NUMBER_OF_THREADS).map {
            Thread {
                repeat(NUMBER_OF_REPS) { rep ->
                    sharedStack.push(rep)
                }
            }.apply(Thread::start)
        }.forEach { it.join() }

        var poppedCounter = 0
        do {
            val poppedValue = sharedStack.pop()
            if (poppedValue != null) poppedCounter += 1
        } while (poppedValue != null)

        assertEquals(NUMBER_OF_REPS * NUMBER_OF_THREADS, poppedCounter)
    }
}
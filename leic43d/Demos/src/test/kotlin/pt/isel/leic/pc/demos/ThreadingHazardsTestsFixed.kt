
package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingHazardsTests::class.java)

// Number of threads used on each test
private const val NUMBER_OF_THREADS = 10

// Number of repetitions performed by each thread
private const val NUMBER_OF_REPS = 1_000_000

private var myCounter: AtomicInteger = AtomicInteger(0)

/**
 * Test suite containing the fixes (using synchronization) for the thread safety hazards from [ThreadingHazardsTests].
 */
class ThreadingHazardsTestsFixed {

    /**
     * Visibility issues, fixed. (More on this later, when we study the underlying memory model)
     */
    @Test
    fun `create thread using a lambda and sharing mutable state, fixed`() {
        log.info("Starting test on thread ${currentThread().name}")

        // TODO:

        log.info("Ending test on thread ${currentThread().name}")
    }

    /**
     * Loss of increments, fixed (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads incrementing a shared counter, fixed`() {
        log.info("Starting test on thread ${currentThread().name}")

        (0 until NUMBER_OF_THREADS).map {
            Thread {
                log.info("Thread $it starting")
                repeat(NUMBER_OF_REPS) { myCounter.incrementAndGet() }
            }.apply {
                start()
            }
        }.forEach { it.join() }

        log.info("myCounter = ${myCounter.get()}")
        log.info("Ending test on thread ${currentThread().name}")
    }
}
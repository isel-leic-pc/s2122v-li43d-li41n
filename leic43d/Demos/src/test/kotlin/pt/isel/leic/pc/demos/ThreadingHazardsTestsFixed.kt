
package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.*
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingHazardsTests::class.java)

// Number of threads used on each test
private const val N_OF_THREADS = 10

// Number of repetitions performed by each thread
private const val N_OF_REPS = 1000000

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

        // TODO:

        log.info("Ending test on thread ${currentThread().name}")
    }
}
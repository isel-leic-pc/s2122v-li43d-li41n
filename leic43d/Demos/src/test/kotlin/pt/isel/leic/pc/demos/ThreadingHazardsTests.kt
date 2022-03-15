
package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.*
import javax.swing.text.html.HTML.Attribute.N
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingHazardsTests::class.java)

// Number of threads used on each test
private const val NUMBER_OF_THREADS = 10

// Number of repetitions performed by each thread
private const val NUMBER_OF_REPS = 1_000_000

private var myCounter: Int = 0

/**
 * Test suite containing thread safety hazards, that is, common errors that result from sharing mutable state without
 * the proper synchronization.
 */
class ThreadingHazardsTests {

    /**
     * Visibility issues. (More on this later, when we study the underlying memory model)
     */
    @Test
    fun `create thread using a lambda and sharing mutable state`() {

        var someValue = 42

        log.info("Starting test on thread ${currentThread().name}")
        Thread {
            log.info("Write to someValue in thread ${currentThread().name}")
            someValue = 95
        }.start()

        sleep(2000)
        log.info("someValue = $someValue in thread ${currentThread().name}")
        log.info("Ending test on thread ${currentThread().name}")
    }

    /**
     * Loss of increments (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads incrementing a shared counter`() {
        log.info("Starting test on thread ${currentThread().name}")

        (0 until NUMBER_OF_THREADS).map {
            Thread {
                log.info("Thread $it starting")
                repeat(NUMBER_OF_REPS) { myCounter += 1 }
            }.apply(Thread::start)
        }.forEach { it.join() }

        log.info("myCounter = $myCounter")
        log.info("Ending test on thread ${currentThread().name}")
    }
}

package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingBasicsTests::class.java)

private fun threadCode() {
    log.info("Function threadCode() running on thread ${currentThread().name}")
    // Simulate some "hard" work... ;)
    sleep(Duration.ofSeconds(2).toMillis())
}

/**
 * Test suite containing
 */
class ThreadingBasicsTests {

    @Test
    fun `create, start and join with a thread`() {
        log.info("Starting test on thread ${currentThread().name}")

        // TODO:

        log.info("Ending test on thread ${currentThread().name}")
    }

    @Test
    fun `multiple threads, one function`() {
        log.info("Starting test on thread ${currentThread().name}")

        // TODO:

        log.info("Ending test on thread ${currentThread().name}")
    }

    @Test
    fun `create thread using a lambda`() {
        log.info("Starting test on thread ${currentThread().name}")

        // TODO:

        log.info("Ending test on thread ${currentThread().name}")
    }

    @Test
    fun `create thread using a derived class`() {
        log.info("Starting test on thread ${currentThread().name}")

        // TODO:

        log.info("Ending test on thread ${currentThread().name}")
    }
}
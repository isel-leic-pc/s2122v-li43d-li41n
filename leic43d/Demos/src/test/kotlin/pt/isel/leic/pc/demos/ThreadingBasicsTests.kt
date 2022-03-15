
package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.*
import java.time.Duration
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingBasicsTests::class.java)

private fun threadCode() {
    log.info("Function threadCode() running on thread ${currentThread().name}")
    // Simulate some "hard" work... ;)
    sleep(Duration.ofSeconds(4).toMillis())
    log.info("Function threadCode() ending on thread ${currentThread().name}")
}

/**
 * Test suite containing examples of how to create, start and synchronize with thread termination.
 */
class ThreadingBasicsTests {

    @Test
    fun `create, start and join with a thread`() {
        log.info("Starting test on thread ${currentThread().name}")
        val thread = Thread(::threadCode)
        thread.start()
        thread.join()
        log.info("Ending test on thread ${currentThread().name}")
    }

    @Test
    fun `multiple threads, one function`() {
        log.info("Starting test on thread ${currentThread().name}")
        val threads = listOf(
            Thread(::threadCode),
            Thread(::threadCode),
            Thread(::threadCode)
        )
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        log.info("Ending test on thread ${currentThread().name}")
    }

    @Test
    fun `create thread using a lambda`() {
        val someValue = 42
        log.info("Starting test on thread ${currentThread().name}")

        val thread = Thread {
            log.info("someValue = $someValue")
            threadCode()
        }
        thread.start()
        thread.join()

        log.info("Ending test on thread ${currentThread().name}")
    }

    /**
     * We can define dedicated computations by deriving from [java.lang.Thread], like this.
     */
    class MySpecialThread : Thread() {
        override fun run() {
            log.info("MySpecialThread.run() thread ${currentThread().name}")
            threadCode()
        }
    }

    @Test
    fun `create thread using a derived class`() {
        log.info("Starting test on thread ${currentThread().name}")
        val thread = MySpecialThread()
        thread.start()
        thread.join()
        log.info("Ending test on thread ${currentThread().name}")
    }
}
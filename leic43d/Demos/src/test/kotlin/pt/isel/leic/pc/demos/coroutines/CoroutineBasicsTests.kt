package pt.isel.leic.pc.demos.coroutines

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.test.Test

private val logger = LoggerFactory.getLogger("Coroutines ")

class CoroutineBasicsTests {

    @Test
    fun `create coroutines`() {
        logger.info("Test starts")
        runBlocking {
            logger.info("runBlocking starts")
            launch {
                logger.info("Coroutine starts")
                delay(2000)
                logger.info("Coroutine ends")
            }
        }
        logger.info("Test ends")
    }
}
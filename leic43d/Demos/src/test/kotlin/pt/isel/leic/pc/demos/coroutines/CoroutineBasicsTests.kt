package pt.isel.leic.pc.demos.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import kotlin.test.Test

private val logger = LoggerFactory.getLogger("Coroutines ")

class CoroutineBasicsTests {

    @Test
    fun `create a coroutine`() {
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

    @Test
    fun `create loads of coroutines`() {
        logger.info("Test starts")
        runBlocking {
            logger.info("runBlocking starts")
            repeat(100_000) {
                launch {
                    delay(2000)
                }
            }
        }
        logger.info("Test ends")
    }

    @Test
    fun `create loads of threads`() {
        logger.info("Test starts")
        val COUNT = 100_000
        val done = CountDownLatch(COUNT)
        repeat(COUNT) {
            Thread {
                Thread.sleep(2000)
                done.countDown()
            }.start()
        }
        done.await()
        logger.info("Test ends")
    }

    @Test
    fun `cancel child coroutines`() {
        logger.info("Test starts")
        runBlocking {
            logger.info("runBlocking starts")
            repeat(5) {
                launch {
                    logger.info("Coroutine starts")
                    delay(5000)
                    logger.info("Coroutine ends")
                }
            }
            delay(2000)
            cancel()
            logger.info("runBlocking end")
        }
        logger.info("Test ends")
    }

    @Test
    fun `create a few sleeping coroutines`() {
        logger.info("Test starts")
        runBlocking {
            logger.info("runBlocking starts")
            repeat(5) {
                launch {
                    logger.info("Coroutine starts")
                    Thread.sleep(2000)
                    logger.info("Coroutine ends")
                }
            }
        }
        logger.info("Test ends")
    }


    @Test
    fun `cancel child coroutines on custom scope`() {
        logger.info("Test starts")
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            logger.info("parent starts")
            repeat(5) {
                launch {
                    logger.info("Child starts")
                    delay(5000)
                    logger.info("Child ends")
                }
            }
            logger.info("parent ends")
        }

        Thread.sleep(2000)
        scope.cancel()

        logger.info("Test ends")
    }
}
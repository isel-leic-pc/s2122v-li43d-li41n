package pt.isel.leic.pc.demos.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch

private val logger = LoggerFactory.getLogger("Coroutines 101")

suspend fun loggedDelay(millis: Long) {
    logger.info("delay starts")
    delay(millis)
    logger.info("delay ends")
}

class CoroutinesBasicsTests {

    @Test
    fun `create coroutines`() {
        logger.info("Test starts")
        runBlocking {
            logger.info("runBlocking starts")
            launch {
                logger.info("Inside launched coroutine")
                delay(2000)
                // Thread.sleep(2000)
            }
            launch {
                logger.info("Inside launched coroutine")
                delay(2000)
                // Thread.sleep(2000)
            }
            logger.info("runBlocking ends")
        }
        logger.info("Test ends")
    }

    @Test
    fun `create one coroutine`() {
        logger.info("Test starts")
        runBlocking {
            val job = async {
                loggedDelay(10000)
                "SLB"
            }
            val spinner = launch {
                while (true) {
                    logger.info("WOW")
                    delay(1000)
                }
            }
            val result = job.await()
            logger.info("Result is $result")
            spinner.cancel()
        }
        logger.info("Test ends")
    }


    @Test
    fun `create many coroutines`() = runBlocking {
        repeat(100_000) {
            launch {
                print('.')
                delay(2000)
                print('.')
            }
        }
    }

    @Test
    fun `create many threads`() {
        val COUNT = 10_000
        val done = CountDownLatch(COUNT)
        repeat(COUNT) {
            Thread {
                print('.')
                Thread.sleep(2000)
                print('.')
                done.countDown()
            }.start()
        }
        done.await()
    }

    @Test
    fun `cancel parent coroutine`() {
        logger.info("before runBlocking")

        runBlocking {
            logger.info("runBlocking starts")

            val parentJob = launch {
                logger.info("Starting parent")
                repeat(5) {
                    launch {
                        try {
                            logger.info("Starting child")
                            while(true) {
                                delay(1000)
                            }
                            logger.info("Ending child")
                        }
                        finally {
                            // Do cleanup
                        }
                    }
                }

                logger.info("Ending parent")
            }

            delay(5000)
            parentJob.cancel()
            logger.info("runBlocking ends")
        }

        logger.info("after runBlocking")
    }
}
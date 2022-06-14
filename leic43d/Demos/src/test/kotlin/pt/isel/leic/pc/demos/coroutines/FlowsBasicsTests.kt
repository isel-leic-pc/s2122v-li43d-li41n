package pt.isel.leic.pc.demos.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.time.ExperimentalTime

private val logger = LoggerFactory.getLogger("Flows 101")

@OptIn(ExperimentalTime::class)
class FlowsBasicsTests {

    private suspend fun getInt(value: Int): Int {
        delay(1000)
        return value
    }

    private suspend fun getIntsEagerSuspend(): List<Int> {
        val result = mutableListOf<Int>()
        repeat(10) {
            logger.info("Producing $it")
            delay(1000)
            result.add(it)
        }
        return result
    }

    private suspend fun getIntsLazySuspend(): Sequence<Int> {
        return sequence {
            repeat(10) {
                logger.info("Producing $it")
                Thread.sleep(1000)
                yield(it)
            }
        }
    }

    private suspend fun getIntsFlow(): Flow<Int> {
        return flow {
            repeat(10) {
                logger.info("Producing $it")
                delay(1000)
                emit(it)
            }
        }
    }
    private fun getIntsLazy(): Sequence<Int> {
        return sequence {
            repeat(10) {
                logger.info("Producing $it")
                yield(it)
            }
        }
    }

    private fun getIntsEager(): List<Int> {
        val result = mutableListOf<Int>()
        repeat(10) {
            logger.info("Producing $it")
            result.add(it)
        }
        return result
    }


    @Test
    fun `sequence of ints test`() {
        val ints = getIntsLazy()
        ints.forEach {
            logger.info("Consuming $it")
        }
    }

    @Test
    fun `list of ints test`() {
        val ints = getIntsEager()
        ints.forEach {
            logger.info("Consuming $it")
        }
    }

    @Test
    fun `call suspend function one int`() {
        logger.info("Tests starts")
        runBlocking {
            val value = getInt(5)
            logger.info("Consuming $value")
        }
        logger.info("Tests ends")
    }

    @Test
    fun `call suspend function list of int`() {
        logger.info("Tests starts")
        runBlocking {
            val values = getIntsEagerSuspend()
            values.forEach {
                delay(500)
                logger.info("Consuming $it")
            }
        }
        logger.info("Tests ends")
    }

    @Test
    fun `call suspend function sequence of int`() {
        logger.info("Tests starts")
        runBlocking {
            val values = getIntsLazySuspend()
            values.forEach {
                delay(500)
                logger.info("Consuming $it")
            }
        }
        logger.info("Tests ends")
    }

    @Test
    fun `call flow of ints`() {
        logger.info("Tests starts")
        runBlocking {
            val values = getIntsFlow()
            values.collect {
                delay(500)
                logger.info("Consuming $it")
            }
        }
        logger.info("Tests ends")
    }

}
package pt.isel.leic.pc.test1

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import pt.isel.leic.pc.demos.logger
import java.util.LinkedList
import kotlin.test.assertContentEquals

private val logger = LoggerFactory.getLogger(Exercise4Tests::class.java)

class Exercise4Tests {

    private fun twoCoroutinesExchangingWith(exchanger: ISuspendingExchanger<Int>) {
        val oneList = buildList { for (i in 0 until 10 step 2) add(i) }
        val otherList = buildList { for (i in 1 until 10 step 2) add(i) }

        logger.info("oneList -> $oneList")
        logger.info("otherList -> $otherList")

        suspend fun exchangeAll(elementsToGive: List<Int>): List<Int> {
            val mine = LinkedList(elementsToGive)
            val others = mutableListOf<Int>()

            while (mine.isNotEmpty()) {
                val toGive = mine.removeFirst()
                val got = exchanger.exchange(toGive)
                others.add(got)
            }

            return others
        }

        val result = runBlocking {
            val oneJob = async { exchangeAll(oneList) }
            val otherJob = async {exchangeAll(otherList) }
            Pair(oneJob.await(), otherJob.await())
        }

        assertContentEquals(oneList, result.second)
        assertContentEquals(otherList, result.first)
    }

    @Test
    fun `exchanging elements with SuspendingExchangerAsPerMyClasses succeeds`() {
        twoCoroutinesExchangingWith(SuspendingExchangerAsPerMyClasses())
    }

    @Test
    fun `exchanging elements with SuspendingExchanger succeeds`() {
        twoCoroutinesExchangingWith(SuspendingExchanger())
    }
}
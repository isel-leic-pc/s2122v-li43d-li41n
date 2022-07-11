package pt.isel.leic.pc.test1

import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Exercise1Tests {

    @OptIn(ExperimentalTime::class)
    @Test
    fun `run executes f0 and f1 in parallel and correctly returns f2s result`() {

        val threadCount = 3
        val executor = Executors.newFixedThreadPool(threadCount)

        val f0Time = 2000L
        val f1Time = 2000L
        val f2Time = 1000L
        val elapsedTime = measureTime {
            val result = run(
                f0 = { Thread.sleep(f0Time); f0Time },
                f1 = { Thread.sleep(f1Time); f1Time },
                f2 = { res0, res1 -> Thread.sleep(f2Time); res0 + res1 + f2Time },
                executor
            )
            assertEquals(f0Time + f1Time + f2Time, result)
        }

        assert(elapsedTime.inWholeMilliseconds < (f0Time + f1Time + f2Time))
    }
}

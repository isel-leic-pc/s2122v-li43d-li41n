package pt.isel.leic.pc.test1

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class Exercise5Tests {

    @Test
    fun `race between fast f0 and slow f1 returns f0 result`() {
        val f0Time = 1000
        val f1Time = 5000
        val elapsedTime = measureTime {
            val result = runBlocking {
                race(
                    f0 = { delay(f0Time.toLong()); f0Time },
                    f1 = { delay(f1Time.toLong()); f1Time }
                )
            }

            assertEquals(f0Time, result)
        }

        assertTrue(elapsedTime.inWholeMilliseconds < f1Time)
    }

    @Test
    fun `race between slow f0 and fast f1 returns f1 result`() {
        val f0Time = 5000
        val f1Time = 1000
        val elapsedTime = measureTime {
            val result = runBlocking {
                race(
                    f0 = { delay(f0Time.toLong()); f0Time },
                    f1 = { delay(f1Time.toLong()); f1Time }
                )
            }

            assertEquals(f1Time, result)
        }

        assertTrue(elapsedTime.inWholeMilliseconds < f0Time)
    }
}
package pt.isel.leic.pc.demos.cache

import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.test.Test

private val log = LoggerFactory.getLogger(CacheTests::class.java)


fun longComputation(key: Int): Int {
    Thread.sleep(200)
    return key + 1
}

const val N_THREADS = 6
const val TIME_BUDGET = 20 * 1000L

class CacheTests {

    private val pool = Executors.newFixedThreadPool(N_THREADS)

    private fun doTest(cache: Cache<Int, Int>) {

        val terminate = AtomicBoolean(false)
        val totalGetCount = AtomicLong(0)

        val latch = CountDownLatch(N_THREADS)

        repeat(N_THREADS) {
            pool.submit {
                val random = Random(System.nanoTime())

                var myGetCount = 0L
                var accumulator = 0L

                while (!terminate.get()) {
                    val key = random.nextInt(100)
                    accumulator += cache.get(key)
                    myGetCount += 1
                }

                log.info("Thread get count = $myGetCount. Acc = $accumulator")
                totalGetCount.addAndGet(myGetCount)
                latch.countDown()
            }
        }

        Thread.sleep(TIME_BUDGET)
        terminate.set(true)
        latch.await()

        log.info("Total get count = ${totalGetCount.get()}")
    }

    @Test
    fun `test throughput of NonScalableCache`() {
        doTest(NonScalableCache(::longComputation))
    }

    @Test
    fun `test throughput of ScalableCache`() {
        doTest(ScalableCache(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun1`() {
        doTest(CacheForFun1(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun2`() {
        doTest(CacheForFun2(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun3`() {
        doTest(CacheForFun3(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun4`() {
        doTest(CacheForFun4(::longComputation))
    }
}

// ns        353
// sc 1067382109
// f1  367119302

// ns        476
// sc 3358590983
// f1  361307991
// f2  145202944

// ns        319
// sc 1321873848
// f1  345734769
// f2  137183591
// f3  313849110

// ns         323
// sc  1090434178
// f1b  337721364
// f2   139750539
// f3b  296411721
// f4   285678552

package pt.isel.leic.pc.demos.cache

import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
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

class CacheTests {

    private val N_THREADS = 6
    private val pool = Executors.newFixedThreadPool(N_THREADS)

    private fun doIt(pool: ExecutorService, cache: Cache<Int, Int>) {

        val latch = CountDownLatch(N_THREADS)
        val terminate = AtomicBoolean(false)
        val totalGets = AtomicLong(0)

        repeat(N_THREADS) {
            pool.submit {
                val random = Random(System.nanoTime())
                var countGets = 0L
                var accumulator = 0L
                while (!terminate.get()) {
                    val key = random.nextInt(100)
                    accumulator += cache.get(key)
                    countGets += 1
                }

                log.info("Thread get count = $countGets. Acc = $accumulator")
                totalGets.addAndGet(countGets)
                latch.countDown()
            }
        }

        Thread.sleep(20000)
        terminate.set(true)

        latch.await()
        log.info("Total get count = ${totalGets.get()}")
    }

    @Test
    fun `test throughput of NonScalableCache`() {
        doIt(pool, NonScalableCache(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun1`() {
        doIt(pool, CacheForFun1(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun2`() {
        doIt(pool, CacheForFun2(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun3`() {
        doIt(pool, CacheForFun3(::longComputation))
    }

    @Test
    fun `test throughput of CacheForFun4`() {
        doIt(pool, CacheForFun4(::longComputation))
    }

    @Test
    fun `test throughput of ScalableCache`() {
        doIt(pool, ScalableCache(::longComputation))
    }
}

// ns        389
// sc 3754974851
// f1  417792296

// ns        363
// sc 1000741323
// f1  406435610
// f2  168060614

// ns        494
// sc 1432774590
// f1  399601237
// f2  149440509
// f3  221835275

// ns        438
// sc 1309614692
// f1  385818626
// f2  151798714
// f3  284256092
// f4  300507685
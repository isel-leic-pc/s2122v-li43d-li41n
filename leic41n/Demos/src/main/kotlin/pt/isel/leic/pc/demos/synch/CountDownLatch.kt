package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CountDownLatch(val initialValue: Int) {
    private var count = initialValue

    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit): Boolean {
        mLock.withLock {
            if (count == 0) return true
            var remainingTime = unit.toNanos(timeout)
            while (true) {
                remainingTime = mCondition.awaitNanos(remainingTime)
                if (count == 0) return true
                if (remainingTime <= 0) return false
            }
        }
    }

    @Throws(InterruptedException::class)
    fun await() {
        mLock.withLock {
            while(count != 0) {
                mCondition.await()
            }
        }
    }

    fun countDown() {
        mLock.withLock {
            if (--count == 0)
                mCondition.signalAll()
        }
    }
}
package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Latch {
    private var signalled = false
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit): Boolean {
        mLock.withLock {
            if (signalled) return true
            var remainingTime = unit.toNanos(timeout)
            while (true) {
                remainingTime = mCondition.awaitNanos(remainingTime)
                if (signalled) return true
                if (remainingTime <= 0) return false
            }
        }
    }

    @Throws(InterruptedException::class)
    fun await() {
        mLock.withLock {
            while(!signalled) {
                mCondition.await()
            }
        }
    }

    fun signal() {
        mLock.withLock {
            signalled = true
            mCondition.signalAll()
        }
    }
}
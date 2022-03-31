package pt.isel.leic.pc.demos.synch

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CountDownLatch(val initialCount: Int) {

    private var counter = initialCount
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    /**
     * Signals (opens) this latch
     */
    fun countDown() {
        mLock.withLock {
            counter -= 1
            if (counter == 0)
                mCondition.signalAll()
        }
    }

    /**
     * Blocks the calling thread until the latch is open
     */
    fun await() {
        mLock.withLock {
            if (counter == 0)
                return

            mCondition.await()

            if (counter == 0)
                return
        }
    }
}
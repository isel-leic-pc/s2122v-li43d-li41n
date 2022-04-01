package pt.isel.leic.pc.demos.synch

import com.sun.tools.javac.jvm.ByteCodes.ret
import java.util.concurrent.TimeUnit
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

            while (true) {
                mCondition.await()

                if (counter == 0)
                    return
            }
        }
    }

    fun await(timeout: Long, unit: TimeUnit): Boolean {
        mLock.withLock {
            if (counter == 0)
                return true

            while (true) {

                val signalled = mCondition.await(timeout, unit)

                if (counter == 0)
                    return true

                if (!signalled)
                    return false
            }

        }
    }
}
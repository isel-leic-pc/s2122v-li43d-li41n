package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.swing.text.html.HTML.Tag.I
import kotlin.concurrent.withLock

class Latch {

    private var signalled = false
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    /**
     * Signals (opens) this latch
     */
    fun signal() {
        mLock.withLock {
            signalled = true
            mCondition.signalAll()
        }
    }

    /**
     * Blocks the calling thread until the latch is open
     */
    @Throws(InterruptedException::class)
    fun await() {
        mLock.withLock {
            if (signalled) return

            while(true) {
                mCondition.await()
                if (signalled) return
            }
        }
    }

    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit): Boolean {
        mLock.withLock {
            if (signalled)
                return true

            var remainingTime = unit.toNanos(timeout)
            while(true) {
                remainingTime = mCondition.awaitNanos(remainingTime)

                if (signalled)
                    return true

                if (remainingTime <= 0)
                    return false
            }
        }
    }

}
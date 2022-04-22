package pt.isel.leic.pc.echo

import java.util.LinkedList
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class ThreadPool(private val maxThreadCount: Int = 1) : Executor {

    private class ThreadPoolWorker(private val pool: ThreadPool) : Thread() {
        override fun run() {
            while (true) {
                val workItem = pool.takeWorkItem(5, TimeUnit.SECONDS) ?: break
                workItem.run()
            }

            pool.threadEnds()
        }
    }

    private var threadCount = 0

    private val workQueue = LinkedList<Runnable>()
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    private fun threadEnds() = mLock.withLock { threadCount -= 1 }

    /**
     * Presumes that [mLock] is acquired
     */
    private fun threadStarts() {
        ThreadPoolWorker(this)
        threadCount += 1
    }

    private fun takeWorkItem(timeout: Long, unit: TimeUnit): Runnable? {
        mLock.withLock {

            if (workQueue.isNotEmpty()) {
                return workQueue.removeFirst()
            }

            var remainingTime = unit.toNanos(timeout)
            while (true) {

                remainingTime = mCondition.awaitNanos(remainingTime)

                if (workQueue.isNotEmpty()) {
                    return workQueue.removeFirst()
                }

                if (remainingTime <= 0)
                    return null
            }
        }
    }

    private fun putWorkItem(workItem: Runnable) {
        mLock.withLock {
            val currentCount = threadCount
            if (threadCount < maxThreadCount && workQueue.isNotEmpty()) {
                threadStarts()
            }
            workQueue.addLast(workItem)
            if (currentCount != threadCount)
                mCondition.signal()
        }
    }

    override fun execute(action: Runnable) {
        putWorkItem(action)
    }
}
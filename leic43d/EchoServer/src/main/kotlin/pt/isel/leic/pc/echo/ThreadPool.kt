@file:Suppress("SameParameterValue")

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
    private var blockedThreads = 0

    private val workQueue = LinkedList<Runnable>()
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    private fun threadEnds() = mLock.withLock { threadCount -= 1 }

    /**
     * Presumes that [mLock] is acquired
     */
    private fun threadStarts() {
        threadCount += 1
        ThreadPoolWorker(this).start()
    }

    private fun takeWorkItem(timeout: Long, unit: TimeUnit): Runnable? {
        mLock.withLock {

            if (workQueue.isNotEmpty()) {
                return workQueue.removeFirst()
            }

            var remainingTime = unit.toNanos(timeout)
            blockedThreads += 1
            try {
                while (true) {

                    remainingTime = mCondition.awaitNanos(remainingTime)

                    if (workQueue.isNotEmpty()) {
                        return workQueue.removeFirst()
                    }

                    if (remainingTime <= 0)
                        return null
                }
            }
            finally {
                blockedThreads -= 1
            }
        }
    }

    private fun putWorkItem(workItem: Runnable) {
        mLock.withLock {
            if (threadCount < maxThreadCount && blockedThreads == 0) {
                threadStarts()
            }

            workQueue.addLast(workItem)
            if (blockedThreads != 0)
                mCondition.signal()
        }
    }

    override fun execute(action: Runnable) {
        putWorkItem(action)
    }
}
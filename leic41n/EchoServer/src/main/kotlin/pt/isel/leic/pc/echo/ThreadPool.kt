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
                val work = pool.takeWorkItem(5, TimeUnit.SECONDS)
                if (work != null) {
                    work.run()
                }
                else {
                    pool.threadEnds()
                    return
                }
            }
        }
    }

    private val workItems = LinkedList<Runnable>()
    private var currentThreadCount: Int = 0
    private var waitingThreadCount: Int = 0

    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    private fun threadEnds() {
        mLock.withLock {
            currentThreadCount -= 1
        }
    }

    private fun maybeStartThread() {
        if (currentThreadCount < maxThreadCount) {
            currentThreadCount += 1
            ThreadPoolWorker(this).start()
        }
    }

    private fun putWorkItem(workItem: Runnable) {
        mLock.withLock {
            workItems.addLast(workItem)
            if (waitingThreadCount != 0) {
                mCondition.signal()
            }
            else {
                maybeStartThread()
            }
        }
    }

    private fun takeWorkItem(timeout: Long, unit: TimeUnit): Runnable? {
        mLock.withLock {

            if (workItems.isNotEmpty()) {
                return workItems.removeFirst()
            }

            var remainingTime = unit.toNanos(timeout)
            waitingThreadCount += 1
            while (true) {

                remainingTime = mCondition.awaitNanos(remainingTime)

                if (workItems.isNotEmpty()) {
                    waitingThreadCount -= 1
                    return workItems.removeFirst()
                }

                if (remainingTime <= 0) {
                    waitingThreadCount -= 1
                    return null
                }
            }
        }
    }

    override fun execute(work: Runnable) {
        putWorkItem(work)
    }

    val queueSize: Int
        get() = mLock.withLock { workItems.size }
}
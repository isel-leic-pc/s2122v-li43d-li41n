package pt.isel.leic.pc.echo

import java.util.LinkedList
import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class ThreadPool(private val threadCount: Int = 1) : Executor {

    private class ThreadPoolWorker(private val pool: ThreadPool) : Thread() {
        override fun run() {
            while (true) {
                pool.takeWorkItem().run()
            }
        }
    }

    private val workQueue = LinkedList<Runnable>()
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    init {
        repeat(threadCount) {
            ThreadPoolWorker(this).start()
        }
    }

    private fun takeWorkItem(): Runnable {
        mLock.withLock {

            if (workQueue.isNotEmpty()) {
                return workQueue.removeFirst()
            }

            while (true) {

                mCondition.await()

                if (workQueue.isNotEmpty()) {
                    return workQueue.removeFirst()
                }
            }
        }
    }

    private fun putWorkItem(workItem: Runnable) {
        mLock.withLock {
            workQueue.addLast(workItem)
            mCondition.signal()
        }
    }

    override fun execute(action: Runnable) {
        putWorkItem(action)
    }
}
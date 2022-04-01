package palbp.laboratory.demos.synch

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A simplification of Android's Handler API.
 * Represents the contract to be supported by message handler's, that is, threads that support execution requests
 * originating on other threads. Execution requests may come in the form of data messages (for which a semantic is
 * already known) or in the form of actions to be _blindly_ executed by the receiver thread. We'll focus on the latter.
 */
interface Handler {
    /**
     * Adds the given action to the message queue. The action will be executed on the thread to which this handler
     * is attached.
     * @return a boolean value indicating if the action was successfully posted on the queue or not.
     */
    fun post(action: () -> Unit): Boolean
}

typealias Action = () -> Unit

/**
 * The Handler implementation
 */
class HandlerThread(private val capacity: Int) : Thread(), Handler {

    private val queue = mutableListOf<Action>()

    // The monitor's lock and condition
    private val mLock = ReentrantLock()
    private val mCondition = mLock.newCondition()

    override fun run() {
        var nextAction: Action? = null
        while (true) {
            mLock.withLock {
                while (queue.isEmpty())
                    mCondition.await()

                nextAction = queue.removeFirst()
            }
            nextAction?.invoke()
        }
    }

    override fun post(action: () -> Unit): Boolean {
        mLock.withLock {
            if (queue.size == capacity)
                return false

            queue.add(action)
            mCondition.signal()
            return true
        }
    }
}
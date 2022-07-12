package pt.isel.leic.pc.test1

import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Implement the following function:
 * <code> fun <A,B,C> run(f0: ()->A, f1: ()->B, f2: (A,B)->C, executor: Executor): C </code>
 * The function returns the value of the expression f2(f0(), f1()), with the following requirements:
 * 1) all functions must be executed by the executor, and not on the thread that called run;
 * 2) the potential parallelism existing in the expression should be explored, assuming that the relative order
 * of evaluation of f0() and f1() is not relevant.
 * For the sake of simplification, assume that none of the functions f0, f1, and f2 throw exceptions.
 * Keep in mind that for satisfying the synchronization requirements, solutions can only make use of explicit monitors
 * (i.e. Lock and Condition implementations).
 */
@Suppress("UNCHECKED_CAST")
fun <A, B, C> run(f0: () -> A, f1: () -> B, f2: (A, B) -> C, executor: Executor): C {

    class ResultHolder<T> {
        private var value: T? = null
        private val guard = ReentrantLock()
        private val hasResult = guard.newCondition()

        fun publish(value: T) {
            guard.withLock {
                this.value = value
                hasResult.signalAll()
            }
        }

        fun awaitResult(): T {
            guard.withLock {
                val observedResult = value
                if (observedResult != null)
                    return observedResult

                while (true) {
                    hasResult.await()

                    val obsResult = value
                    if (obsResult != null)
                        return obsResult
                }
            }
        }
    }

    val f0Result = ResultHolder<A>()
    val f1Result = ResultHolder<B>()
    executor.execute { f0Result.publish(f0()) }
    executor.execute { f1Result.publish(f1()) }

    val a = f0Result.awaitResult()
    val b = f1Result.awaitResult()

    val f2Result = ResultHolder<C>()
    executor.execute { f2Result.publish(f2(a, b)) }

    return f2Result.awaitResult()
}


package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingHazardsTests::class.java)

// Number of threads used on each test
private const val NUMBER_OF_THREADS = 10

// Number of repetitions performed by each thread
private const val NUMBER_OF_REPS = 1_000_000

private var myCounter: AtomicInteger = AtomicInteger(0)

/**
 * Test suite containing the fixes (using synchronization) for the thread safety hazards from [ThreadingHazardsTests].
 */
class ThreadingHazardsTestsFixed {

    /**
     * Visibility issues, fixed. (More on this later, when we study the underlying memory model)
     */
    @Test
    fun `create thread using a lambda and sharing mutable state, fixed`() {
        log.info("Starting test on thread ${currentThread().name}")

        // TODO:

        log.info("Ending test on thread ${currentThread().name}")
    }

    /**
     * Loss of increments, fixed (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads incrementing a shared counter, fixed`() {
        log.info("Starting test on thread ${currentThread().name}")

        (0 until NUMBER_OF_THREADS).map {
            Thread {
                log.info("Thread $it starting")
                repeat(NUMBER_OF_REPS) { myCounter.incrementAndGet() }
            }.apply {
                start()
            }
        }.forEach { it.join() }

        log.info("myCounter = ${myCounter.get()}")
        log.info("Ending test on thread ${currentThread().name}")
    }


    /**
     * Thread-safe stack implementation
     */
    class ThreadSafeMutableStack<T> {

        private data class Node<T>(val value: T, val next: Node<T>?)
        private var top: Node<T>? = null
        private val lock = ReentrantLock()

        fun push(value: T) {
            // TODO: Must fix
            lock.lock()
            top = Node(value, top)
            lock.unlock()
        }

        fun pop(): T? {
            var result:T? = null
            // TODO: Must fix
            lock.lock()
            result = if (top != null) {
                val value = top?.value
                top = top?.next
                value
            } else null
            lock.unlock()
            return result
        }
    }

    private val sharedStack = ThreadSafeMutableStack<Int>()

    /**
     * Loss of insertions (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads inserting values to a shared list, fixed`() {
        log.info("Starting test on thread ${currentThread().name}")

        (0 until NUMBER_OF_THREADS).map {
            Thread {
                log.info("Thread $it starting")
                repeat(NUMBER_OF_REPS) { rep ->
                    sharedStack.push(rep)
                }
            }.apply(Thread::start)
        }.forEach { it.join() }

        var poppedCounter = 0
        do {
            val poppedValue = sharedStack.pop()
            if (poppedValue != null) poppedCounter += 1
        } while (poppedValue != null)

        log.info("poppedCounter = $poppedCounter")
        log.info("Ending test on thread ${currentThread().name}")
    }
}
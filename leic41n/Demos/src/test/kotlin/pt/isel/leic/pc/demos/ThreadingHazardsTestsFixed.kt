
package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingHazardsTests::class.java)

// Number of threads used on each test
private const val N_OF_THREADS = 10

// Number of repetitions performed by each thread
private const val N_OF_REPS = 1000000

/**
 * The shared counter :p (shared mutable state!!!)
 */
private val sharedCounter: AtomicInteger = AtomicInteger(0)


/**
 * Test suite containing the fixes (using synchronization) for the thread safety hazards from [ThreadingHazardsTests].
 */
class ThreadingHazardsTestsFixed {

    /**
     * Visibility issues, fixed. (More on this later, when we study the underlying memory model)
     */
    @Test
    fun `create thread using a lambda and sharing mutable state, fixed`() {
        val isThisASharedCounter = AtomicInteger(0)
        log.info("Starting test on thread ${currentThread().name}")

        (0 until N_OF_THREADS).map {
            Thread {
                repeat(N_OF_REPS) { isThisASharedCounter.incrementAndGet() }
            }.apply(Thread::start)
        }.forEach(Thread::join)

        log.info("shared counter = ${isThisASharedCounter.get()}")
        log.info("Ending test on thread ${currentThread().name}")
    }

    /**
     * Loss of increments, fixed (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads incrementing a shared counter, fixed`() {
        log.info("Starting test on thread ${currentThread().name}")

        (0 until N_OF_THREADS).map {
            Thread {
                repeat(N_OF_REPS) { sharedCounter.incrementAndGet() }
            }.apply(Thread::start)
        }.forEach(Thread::join)

        log.info("shared counter = ${sharedCounter.get()}")
        log.info("Ending test on thread ${currentThread().name}")
    }

    class ThreadSafeMutableStack<T> {

        private data class Node<T>(val value: T, val next: Node<T>? = null)
        private var top: Node<T>? = null
        private val lock = ReentrantLock()

        fun push(value: T) {
            lock.lock()
            top = Node(value, top)
            lock.unlock()
        }

        fun pop(): T? {
            lock.lock()
            val result = if(top != null) {
                val value = top?.value
                top = top?.next
                value
            }
            else null
            lock.unlock()
            return result
        }
    }

    private val sharedStack = ThreadSafeMutableStack<Int>()

    /**
     * Loss of insertions, fixed (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads inserting in a shared list, fixed`() {
        log.info("Starting test on thread ${currentThread().name}")

        (0 until N_OF_THREADS).map {
            Thread {
                repeat(N_OF_REPS) {
                    sharedStack.push(it)
                }
            }.apply(Thread::start)
        }.forEach(Thread::join)

        var poppedCounter = 0
        do {
            val poppedValue = sharedStack.pop()
            if (poppedValue != null) poppedCounter += 1
        } while (poppedValue != null)

        log.info("poppedCounter = $poppedCounter")
        log.info("Ending test on thread ${currentThread().name}")
    }

}
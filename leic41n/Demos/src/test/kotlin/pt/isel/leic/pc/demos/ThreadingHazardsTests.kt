
package pt.isel.leic.pc.demos

import org.slf4j.LoggerFactory
import java.lang.Thread.*
import kotlin.test.Test

private val log = LoggerFactory.getLogger(ThreadingHazardsTests::class.java)

// Number of threads used on each test
private const val N_OF_THREADS = 10

// Number of repetitions performed by each thread
private const val N_OF_REPS = 1000000

/**
 * The shared counter :p (shared mutable state!!!)
 */
private var sharedCounter = 0

/**
 * Test suite containing thread safety hazards, that is, common errors that result from sharing mutable state without
 * the proper synchronization.
 */
class ThreadingHazardsTests {

    /**
     * Visibility issues. (More on this later, when we study the underlying memory model)
     */
    @Test
    fun `create thread using a lambda and sharing mutable state`() {
        var isThisASharedCounter: Int = 0
        log.info("Starting test on thread ${currentThread().name}")

        (0 until N_OF_THREADS).map {
            Thread {
                repeat(N_OF_REPS) { isThisASharedCounter += 1 }
            }.apply(Thread::start)
        }.forEach(Thread::join)

        log.info("shared counter = $isThisASharedCounter")
        log.info("Ending test on thread ${currentThread().name}")
    }

    /**
     * Loss of increments (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads incrementing a shared counter`() {
        log.info("Starting test on thread ${currentThread().name}")

        (0 until N_OF_THREADS).map {
            Thread {
                repeat(N_OF_REPS) { sharedCounter += 1 }
            }.apply(Thread::start)
        }.forEach(Thread::join)

        log.info("shared counter = $sharedCounter")
        log.info("Ending test on thread ${currentThread().name}")
    }

    class MutableStack<T> {

        private data class Node<T>(val value: T, val next: Node<T>? = null)
        private var top: Node<T>? = null

        fun push(value: T) {
            top = Node(value, top)
        }

        fun pop(): T? {
            return if(top != null) {
                val value = top?.value
                top = top?.next
                value
            }
            else null
        }
    }

    private val sharedStack = MutableStack<Int>()

    /**
     * Loss of insertions (an example of the anomaly known as Lost Updates)
     */
    @Test
    fun `multiple threads inserting in a shared list`() {
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
package pt.isel.leic.pc.demos.lockfree

import java.util.concurrent.atomic.AtomicInteger

@Suppress("ControlFlowWithEmptyBody")
class LockFreeCounter(initialValue: Int) {

    private val _value = AtomicInteger(initialValue)

    val value: Int
        get() = _value.get()

    private fun doSafely(operation: (Int) -> Int): Int {
        while (true) {
            val observedValue = _value.get()
            if(_value.compareAndSet(observedValue, operation(observedValue)))
                return operation(observedValue)
        }
    }

    fun increment() = doSafely { it + 1 }
    fun decrement() = doSafely { it - 1 }
}
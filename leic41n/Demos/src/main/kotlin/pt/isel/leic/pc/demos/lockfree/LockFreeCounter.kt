package pt.isel.leic.pc.demos.lockfree

import java.util.concurrent.atomic.AtomicInteger

class LockFreeCounter(initialValue: Int = 0) {

    private val _value = AtomicInteger(initialValue)

    val value: Int
        get() = _value.get()

    fun increment(): Int {
        while(true) {
            val observedValue = _value.get()
            if (_value.compareAndSet(observedValue, observedValue + 1))
                return observedValue + 1
        }
    }

    fun decrement(): Unit {
        //_value.compareAndSet(_value.get(), _value.get() - 1)
    }
}


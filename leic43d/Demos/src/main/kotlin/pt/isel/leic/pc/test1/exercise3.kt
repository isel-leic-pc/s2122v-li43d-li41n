package pt.isel.leic.pc.test1

import java.util.concurrent.atomic.AtomicInteger

/**
 * Implement, without using locks, a thread-safe version of the UnsafeContainer class that stores a set of values
 * and the number of times those values can be consumed.
 *
 * class UnsafeValue<T>(val value: T, var Lives: Int)
 * class UnsafeContainer<T>(private val values: Array<UnsafeValue<T>>){
 *    private var index = 0
 *    fun consume(): T? {
 *       while(index < values.size) {
 *          if (values[index].lives > 0) {
 *             values[index].lives -= 1
 *             return values[index].value
 *          }
 *          index += 1
 *       }
 *       return null
 *    }
 * }
 *
 * As an example, the container constructed by Container(arrayOf(Value("isel", 3), Value("pc", 4))) returns, through
 * the consume method, the string "isel" three times and the string "pc" four times. After that, all calls to consume
 * return null.
 */


// This is an UNSAFE implementation. TODO: Fix it!
class Value<T>(val value: T, private val initialLives: Int) {
    val lives = AtomicInteger(initialLives)
}


class Container<T>(private val values: Array<Value<T>>){

    private val index = AtomicInteger(0)

    fun consume(): T? {

        while(true) {
            val observedIndex = index.get()
            if (observedIndex >= values.size)
                return null

            val observedValue = values[observedIndex]
            val observedLives = observedValue.lives.get()

            if (observedLives != 0) {
                if (observedValue.lives.compareAndSet(observedLives, observedLives - 1)) {
                    return observedValue.value
                }
            }
            else {
                index.compareAndSet(observedIndex, observedIndex + 1)
            }
        }
    }
}

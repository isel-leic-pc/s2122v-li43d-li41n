package pt.isel.leic.pc.demos.synch

import kotlin.test.assertEquals

class MyFutureTests {

    fun someTest() {
        val value = 20
        fun someFunction(): MyFuture<Int> {
            val result = MyCompletableFuture<Int>()
            Thread {
                Thread.sleep(5000)
                result.set(value)
            }
            return result
        }

        val result = someFunction()
        assertEquals(value, result.get())
    }
}
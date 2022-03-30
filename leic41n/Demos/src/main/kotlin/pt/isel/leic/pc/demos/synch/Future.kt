package pt.isel.leic.pc.demos.synch

import java.util.concurrent.TimeUnit

interface MyFuture<T> {

    @Throws(InterruptedException::class)
    fun get(): T

    @Throws(InterruptedException::class)
    fun get(timeout: Long, unit: TimeUnit): T
}

class MyCompletableFuture<T> : MyFuture<T> {
    override fun get(): T {
        TODO("Not yet implemented")
    }

    override fun get(timeout: Long, unit: TimeUnit): T {
        TODO("Not yet implemented")
    }

    fun set(value: T) {
        TODO()
    }
}
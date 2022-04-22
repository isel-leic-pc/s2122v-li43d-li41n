package pt.isel.leic.pc.demos.cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Cache<K, V> {
    fun get(key: K): V
}

class NonScalableCache<K, V>(private val compute: (K) -> V) : Cache<K, V> {

    private val guard = ReentrantLock()
    private val cache = HashMap<K, V>()

    override fun get(key: K): V {
        guard.withLock {
            val value = cache[key]
            return value ?: compute(key).also { cache[key] = it }
        }
    }
}

class ScalableCache<K, V>(private val compute: (K) -> V) : Cache<K, V> {
    private val cache = ConcurrentHashMap<K, V>()
    override fun get(key: K): V = cache.computeIfAbsent(key) { compute(key) }
}

class CacheForFun1<K, V>(private val compute: (K) -> V) : Cache<K, V> {
    private val guard = ReentrantLock()
    private val cache = HashMap<K, V>()

    override fun get(key: K): V {
        guard.withLock {
            val value = cache[key]
            if (value != null)
                return value
        }

        val value = compute(key)

        guard.withLock {
            cache.put(key, value)
        }

        return value
    }
}

class CacheForFun2<K, V>(private val compute: (K) -> V) : Cache<K, V> {

    private class CacheFuture<K, V>(private val compute: (K) -> V) {

        private val guard = ReentrantLock()
        private var value: V? = null

        fun get(key: K): V = guard.withLock {
            value ?: compute(key).also { value = it }
        }
    }

    private val guard = ReentrantLock()
    private val cache = HashMap<K, CacheFuture<K, V>>()

    override fun get(key: K): V {
        val value = guard.withLock {
            val future = cache[key]
            future ?: CacheFuture(compute).also { cache[key] = it }
        }
        return value.get(key)
    }
}



package pt.isel.leic.pc.demos.cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Cache<K, V> {
    fun get(key: K): V
}

class NonScalableCache<K, V>(private val compute: (K) -> V) : Cache<K, V> {

    private val cache = HashMap<K, V>()
    private val guard = ReentrantLock()

    override fun get(key: K): V =
        guard.withLock {
            cache[key] ?: compute(key).also { cache[key] = it }
        }
}

class CacheForFun1<K, V>(private val compute: (K) -> V) : Cache<K, V> {

    private val cache = HashMap<K, V>()
    private val guard = ReentrantLock()

    override fun get(key: K): V {
        guard.withLock {
            val value = cache[key]
            if (value != null)
                return value
        }

        val value = compute(key)

        guard.withLock {
            cache[key] = value
        }

        return value
    }
}


class ScalableCache<K, V>(private val compute: (K) -> V) : Cache<K, V> {
    private val cache = ConcurrentHashMap<K, V>()
    override fun get(key: K): V = cache.computeIfAbsent(key) { compute(key) }
}

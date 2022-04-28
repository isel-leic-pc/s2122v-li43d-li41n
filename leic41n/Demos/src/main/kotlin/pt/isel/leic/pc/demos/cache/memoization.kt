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

/**
 * Flawed implementation
 */
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

class CacheForFun2<K, V>(private val compute: (K) -> V) : Cache<K, V> {

    private val cache = HashMap<K, CacheFuture<K, V>>()
    private val guard = ReentrantLock()

    private class CacheFuture<K, V>(private val compute: (K) -> V) {

        private var value: V? = null
        private val entryGuard = ReentrantLock()

        fun get(key: K): V {
            entryGuard.withLock {
                if (value == null)
                    value = compute(key)

                return value as V
            }
        }
    }

    override fun get(key: K): V {

        var valueFuture: CacheFuture<K, V>? = null
        guard.withLock {
            valueFuture = cache[key]
            if (valueFuture == null) {
                valueFuture = CacheFuture(compute)
                cache[key] = valueFuture as CacheFuture<K, V>
            }
        }
        return (valueFuture as CacheFuture<K, V>).get(key)
    }
}

/**
 * Flawed implementation.
 */
class CacheForFun3<K, V>(private val compute: (K) -> V) : Cache<K, V> {

    private val cache = HashMap<K, CacheFuture<K, V>>()
    private val guard = ReentrantLock()

    private class CacheFuture<K, V>(private val compute: (K) -> V) {

        private var value: V? = null
        private val entryGuard = ReentrantLock()

        fun get(key: K): V {

            if (value != null) {
                return value as V
            }

            entryGuard.withLock {

                if (value == null)
                    value = compute(key)

                return value as V
            }
        }
    }

    override fun get(key: K): V {

        var valueFuture: CacheFuture<K, V>? = null
        guard.withLock {
            valueFuture = cache[key]
            if (valueFuture == null) {
                valueFuture = CacheFuture(compute)
                cache[key] = valueFuture as CacheFuture<K, V>
            }
        }
        return (valueFuture as CacheFuture<K, V>).get(key)
    }
}

class CacheForFun4<K, V>(private val compute: (K) -> V) : Cache<K, V> {

    private val cache = HashMap<K, CacheFuture<K, V>>()
    private val guard = ReentrantLock()

    private class CacheFuture<K, V>(private val compute: (K) -> V) {

        @Volatile private var value: V? = null
        private val entryGuard = ReentrantLock()

        fun get(key: K): V {

            if (value != null) {
                return value as V
            }

            entryGuard.withLock {

                if (value == null)
                    value = compute(key)

                return value as V
            }
        }
    }

    override fun get(key: K): V {

        var valueFuture: CacheFuture<K, V>? = null
        guard.withLock {
            valueFuture = cache[key]
            if (valueFuture == null) {
                valueFuture = CacheFuture(compute)
                cache[key] = valueFuture as CacheFuture<K, V>
            }
        }
        return (valueFuture as CacheFuture<K, V>).get(key)
    }
}

class ScalableCache<K, V>(private val compute: (K) -> V) : Cache<K, V> {
    private val cache = ConcurrentHashMap<K, V>()
    override fun get(key: K): V = cache.computeIfAbsent(key) { compute(key) }
}

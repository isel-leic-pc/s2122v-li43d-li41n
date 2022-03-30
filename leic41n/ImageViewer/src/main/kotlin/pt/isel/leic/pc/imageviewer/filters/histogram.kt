@file:Suppress("unused")

package pt.isel.leic.pc.imageviewer.filters

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toAwtImage
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

data class Histogram(val values: List<Pair<Int, Int>>)

fun MutableMap<Int, Int>.safeIncrement(key: Int) {
    // We could use the @Synchronized annotation instead
    synchronized(this) {
        this[key] = (this[key] ?: 0) + 1
    }
}

/**
 * Computes the image's luminosity histogram (single threaded version - ST)
 */
fun computeLuminanceHistogramST(imageBitmap: ImageBitmap): Histogram {
    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    val counts = mutableMapOf<Int, Int>()
    val elapsed = measureTimeMillis {
        bufferedImage.forEach {
            val grayscaleValue = (it.luminance() * 100).toInt()
            counts[grayscaleValue] = (counts[grayscaleValue] ?: 0) + 1
        }
    }
    filtersLogger.info("Computed Histogram (ST) in $elapsed ms")
    return Histogram(counts.map { Pair(it.key, it.value) })
}

fun computeLuminanceHistogramMTSharedIntrinsicLock(imageBitmap: ImageBitmap): Histogram {
    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    val counts = mutableMapOf<Int, Int>()
    val elapsed = measureTimeMillis {
        val latch = CountDownLatch(EXPECTED_CORE_COUNT)
        repeat(EXPECTED_CORE_COUNT) {
            Thread {
                val (xBounds, yBounds) = computePartitionBounds(
                    width = bufferedImage.width,
                    height = bufferedImage.height,
                    partitionCount = EXPECTED_CORE_COUNT,
                    partitionIndex = it
                )
                bufferedImage.forEach(xBounds = xBounds, yBounds = yBounds) {
                    val grayscaleValue = (it.luminance() * 100).toInt()
                    counts.safeIncrement(grayscaleValue)
                }
                latch.countDown()
            }.start()
        }
        latch.await()
    }
    filtersLogger.info("Computed Histogram (MT) with shared map and intrinsic lock in $elapsed ms")
    return Histogram(counts.map { Pair(it.key, it.value) })
}

fun computeLuminanceHistogramMTShared(imageBitmap: ImageBitmap): Histogram {
    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    val counts = ConcurrentHashMap<Int, AtomicInteger>()
    val elapsed = measureTimeMillis {
        val latch = CountDownLatch(EXPECTED_CORE_COUNT)
        repeat(EXPECTED_CORE_COUNT) {
            Thread {
                val (xBounds, yBounds) = computePartitionBounds(
                    width = bufferedImage.width,
                    height = bufferedImage.height,
                    partitionCount = EXPECTED_CORE_COUNT,
                    partitionIndex = it
                )
                bufferedImage.forEach(xBounds = xBounds, yBounds = yBounds) {
                    val grayscaleValue = (it.luminance() * 100).toInt()
                    counts.computeIfAbsent(grayscaleValue) { AtomicInteger(0) }.incrementAndGet()
                }
                latch.countDown()
            }.start()
        }
        latch.await()
    }
    filtersLogger.info("Computed Histogram (MT) with shared map in $elapsed ms")
    return Histogram(counts.map { Pair(it.key, it.value.get()) })
}

fun computeLuminanceHistogramMTPrivate(imageBitmap: ImageBitmap): Histogram {
    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    val counts = MutableList(size = EXPECTED_CORE_COUNT) { mutableMapOf<Int, Int>() }
    val globalCounts = counts[0]
    val elapsed = measureTimeMillis {
        val latch = CountDownLatch(EXPECTED_CORE_COUNT)
        repeat(EXPECTED_CORE_COUNT) {
            Thread {
                val (xBounds, yBounds) = computePartitionBounds(
                    width = bufferedImage.width,
                    height = bufferedImage.height,
                    partitionCount = EXPECTED_CORE_COUNT,
                    partitionIndex = it
                )
                val privateCounts = counts[it]
                bufferedImage.forEach(xBounds = xBounds, yBounds = yBounds) {
                    val grayscaleValue = (it.luminance() * 100).toInt()
                    privateCounts[grayscaleValue] = (privateCounts[grayscaleValue] ?: 0) + 1
                }

                latch.countDown()
            }.start()
        }
        latch.await()

        // Compute global histogram
        (1 until counts.size).forEach {
            val partialCounts = counts[it]
            partialCounts.forEach { entry ->
                globalCounts.merge(entry.key, entry.value) { current, toMerge -> current + toMerge }
            }
        }
    }

    filtersLogger.info("Computed Histogram (MT) with private maps in $elapsed ms")
    return Histogram(globalCounts.map { Pair(it.key, it.value) })
}

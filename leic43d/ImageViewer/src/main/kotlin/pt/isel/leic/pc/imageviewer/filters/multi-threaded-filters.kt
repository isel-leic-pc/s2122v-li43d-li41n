@file:Suppress("unused")

package pt.isel.leic.pc.imageviewer.filters

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis

/**
 * Converts the given image to its grayscale version (multithreaded version - MT)
 * (It uses the Luminosity method)
 *
 * Implementation notes:
 *  - Multithreaded version that uses a fixed number of threads (EXPECTED_CORE_COUNT), regardless of the actual number
 *  of cores in the target machine
 */
fun convertToGrayScaleMT(imageBitmap: ImageBitmap): ImageBitmap {

    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    filtersLogger.info("Converting to gray scale (Single threaded approach)")
    filtersLogger.info("Image size is: width = ${bufferedImage.width}; height = ${bufferedImage.height}")

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
                bufferedImage.applyTransform(xBounds = xBounds, yBounds = yBounds) {
                    val grayscaleValue = it.luminance()
                    Color(
                        red = grayscaleValue,
                        green = grayscaleValue,
                        blue = grayscaleValue,
                        colorSpace = it.colorSpace,
                        alpha = it.alpha
                    )
                }
                latch.countDown()
            }.start()
        }
        latch.await()
    }

    val result = bufferedImage.toComposeImageBitmap()
    filtersLogger.info("Converted to gray scale in $elapsed ms")
    return result
}

/**
 * Adjusts the brightness of the given image, producing the new image version (multithreaded version - MT)
 *
 * Implementation notes:
 *  - Multithreaded version that uses a fixed number of threads (EXPECTED_CORE_COUNT), regardless of the actual number
 *  of cores in the target machine
 *
 * @param imageBitmap   the image to be processed
 * @param delta         the brightness percentage variation in the interval [-1.0 ... 1.0]
 */
fun adjustBrightnessMT(imageBitmap: ImageBitmap, delta: Float): ImageBitmap {

    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    filtersLogger.info("Adjusting brightness (Single threaded approach): ")
    filtersLogger.info("Image size is: width = ${bufferedImage.width}; height = ${bufferedImage.height}")
    val elapsedMillis = measureTimeMillis {
        val latch = CountDownLatch(EXPECTED_CORE_COUNT)
        repeat(EXPECTED_CORE_COUNT) {
            Thread {
                val (xBounds, yBounds) = computePartitionBounds(
                    width = bufferedImage.width,
                    height = bufferedImage.height,
                    partitionCount = EXPECTED_CORE_COUNT,
                    partitionIndex = it
                )
                bufferedImage.applyTransform(xBounds = xBounds, yBounds = yBounds) {
                    Color(
                        red = (it.red + delta).coerceInRGB(),
                        green = (it.green + delta).coerceInRGB(),
                        blue = (it.blue + delta).coerceInRGB(),
                        colorSpace = it.colorSpace,
                        alpha = it.alpha
                    )
                }
                latch.countDown()
            }.start()
        }
        latch.await()
    }
    val result = bufferedImage.toComposeImageBitmap()
    filtersLogger.info("Adjusted brightness in $elapsedMillis ms")
    return result
}

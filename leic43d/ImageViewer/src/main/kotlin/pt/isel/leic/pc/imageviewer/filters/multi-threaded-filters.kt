@file:Suppress("unused")

package pt.isel.leic.pc.imageviewer.filters

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage
import kotlin.system.measureTimeMillis

private const val EXPECTED_CORE_COUNT = 6

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
    println("Converting to gray scale (Single threaded approach)")
    println("Image size is: width = ${bufferedImage.width}; height = ${bufferedImage.height}")

    val elapsed = measureTimeMillis {
        // TODO
    }

    val result = bufferedImage.toComposeImageBitmap()
    println("Converted to gray scale in $elapsed ms")
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
    println("Adjusting brightness (Single threaded approach): ")
    println("Image size is: width = ${bufferedImage.width}; height = ${bufferedImage.height}")
    val elapsedMillis = measureTimeMillis {
        // TODO
    }
    val result = bufferedImage.toComposeImageBitmap()
    println("Adjusted brightness in $elapsedMillis ms")
    return result
}

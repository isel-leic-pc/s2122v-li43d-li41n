@file:Suppress("unused")

package pt.isel.leic.pc.imageviewer.filters

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage
import kotlin.system.measureTimeMillis

/**
 * Converts the given image to its grayscale version (single threaded version - ST)
 * (It uses the Luminosity method)
 */
fun convertToGrayScaleST(imageBitmap: ImageBitmap): ImageBitmap {

    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    println("Converting to gray scale (Single threaded approach)")
    println("Image size is: width = ${bufferedImage.width}; height = ${bufferedImage.height}")
    val elapsed = measureTimeMillis {
        bufferedImage.applyTransform {
            val grayscaleValue = it.luminance()
            Color(
                red = grayscaleValue,
                green = grayscaleValue,
                blue = grayscaleValue,
                colorSpace = it.colorSpace,
                alpha = it.alpha
            )
        }
    }
    val result = bufferedImage.toComposeImageBitmap()
    println("Converted to gray scale in $elapsed ms")
    return result
}

/**
 * Adjusts the brightness of the given image, producing the new image version (single threaded version - ST)
 *
 * @param imageBitmap   the image to be processed
 * @param delta         the brightness percentage variation in the interval [-1.0 ... 1.0]
 */
fun adjustBrightnessST(imageBitmap: ImageBitmap, delta: Float): ImageBitmap {

    val bufferedImage: BufferedImage = imageBitmap.toAwtImage()
    println("Adjusting brightness (Single threaded approach): ")
    println("Image size is: width = ${bufferedImage.width}; height = ${bufferedImage.height}")
    val elapsedMillis = measureTimeMillis {
        bufferedImage.applyTransform {
            Color(
                red = (it.red + delta).coerceInRGB(),
                green = (it.green + delta).coerceInRGB(),
                blue = (it.blue + delta).coerceInRGB(),
                colorSpace = it.colorSpace,
                alpha = it.alpha
            )
        }
    }
    val result = bufferedImage.toComposeImageBitmap()
    println("Adjusted brightness in $elapsedMillis ms")
    return result
}

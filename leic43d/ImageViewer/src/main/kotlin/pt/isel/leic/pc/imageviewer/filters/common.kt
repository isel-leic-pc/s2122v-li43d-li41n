package pt.isel.leic.pc.imageviewer.filters

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.awt.image.BufferedImage

/**
 * Extension function that coerces this float to the acceptable RGB interval [0.0 ... 1.0]
 */
fun Float.coerceInRGB(): Float = if (this <= 0.0) 0.0f else if (this >= 1.0) 1.0f else this

/**
 * Extension function that applies the given transformation for each pixel of this [BufferedImage] that is within the
 * specified bounds. The function is destructive, meaning, this instance is modified by the call.
 */
fun BufferedImage.applyTransform(
    xBounds: IntRange = 0 until width,
    yBounds: IntRange = 0 until height,
    transform: (Color) -> Color
): BufferedImage {
    for (x in xBounds) {
        for (y in yBounds) {
            val pixel = Color(getRGB(x, y))
            val changedPixel = transform(pixel)
            setRGB(x, y, changedPixel.toArgb())
        }
    }
    return this
}

/**
 * Computes the bounds of rectangular partitions in a bi-dimensional space.
 * @param width             the width of the space to be partitioned
 * @param height            the height of the space to be partitioned
 * @param partitionCount    the number of partitions. The algorithm only contemplates even numbers
 * @param partitionIndex    the partitioned index, in the interval [0 ... partitionCount[
 * @return a [Pair] instance containing the partitions x-axis bounds and the y-axis bounds, respectively.
 */
fun computePartitionBounds(width: Int, height: Int, partitionCount: Int, partitionIndex: Int): Pair<IntRange, IntRange> {

    val rowCount = 2
    val columnCount = partitionCount / rowCount
    require(partitionCount % rowCount == 0)
    require(partitionIndex in 0 until partitionCount)
    require(width >= partitionCount / rowCount && height >= rowCount) { "Space to partition isn't large enough" }

    val blockHeight = height / rowCount
    val yLowerBound: Int = (partitionIndex / columnCount) * blockHeight
    val yUpperBound: Int = kotlin.math.min(yLowerBound + blockHeight, height)

    val blockWidth = width / columnCount
    val xLowerBound: Int = (partitionIndex % columnCount) * blockWidth
    val xUpperBound: Int = kotlin.math.min(xLowerBound + blockWidth, width)

    return Pair(xLowerBound until xUpperBound, yLowerBound until yUpperBound)
}

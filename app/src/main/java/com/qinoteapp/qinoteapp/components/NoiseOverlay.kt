package com.qinoteapp.qinoteapp.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.luminance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun NoiseOverlay(
    alpha: Float = if (QiTheme.colors.Background.luminance() < 0.5f) 0.05f else 0.03f,
    modifier: Modifier = Modifier
) {
    val noiseBitmap = remember {
        generateNoiseBitmap(256, 256)
    }
    val imageBitmap = remember(noiseBitmap) {
        noiseBitmap.asImageBitmap()
    }

    Canvas(modifier = modifier) {
        drawNoise(imageBitmap, alpha)
    }
}

private fun generateNoiseBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val random = java.util.Random(42)
    for (x in 0 until width) {
        for (y in 0 until height) {
            val value = random.nextInt(256)
            val color = Color(value, value, value, 255)
            bitmap.setPixel(x, y, android.graphics.Color.argb(
                (color.alpha * 255).toInt(),
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            ))
        }
    }
    return bitmap
}

private fun DrawScope.drawNoise(noise: ImageBitmap, alpha: Float) {
    val tileWidth = noise.width.toFloat()
    val tileHeight = noise.height.toFloat()
    var x = 0f
    while (x < size.width) {
        var y = 0f
        while (y < size.height) {
            drawImage(
                image = noise,
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                alpha = alpha
            )
            y += tileHeight
        }
        x += tileWidth
    }
}

package com.qinoteapp.qinoteapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

private data class OrbDef(
    val xFrac: Float,
    val yFrac: Float,
    val baseHue: Float,
    val phaseIndex: Int,
    val radiusFrac: Float,
    val intensity: Float
)

@Composable
fun AuroraGlow(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)),
        exit = fadeOut(tween(600))
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "aurora")

        val phase1 by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "phase1"
        )
        val phase2 by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(2000)
            ),
            label = "phase2"
        )
        val phase3 by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(4000)
            ),
            label = "phase3"
        )
        val hueDrift by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = EaseInOut),
                repeatMode = RepeatMode.Restart
            ),
            label = "hueDrift"
        )
        val breathAlpha by infiniteTransition.animateFloat(
            initialValue = 0.75f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathAlpha"
        )

        val phases = listOf(phase1, phase2, phase3)

        val orbs = listOf(
            OrbDef(0.08f, -0.12f, 0f, 0, 0.55f, 1.0f),
            OrbDef(0.35f, -0.15f, 40f, 1, 0.6f, 0.9f),
            OrbDef(0.65f, -0.13f, 80f, 2, 0.55f, 1.0f),
            OrbDef(0.92f, -0.1f, 130f, 0, 0.48f, 0.85f),
            OrbDef(0.12f, 1.12f, 175f, 1, 0.52f, 0.9f),
            OrbDef(0.5f, 1.15f, 220f, 0, 0.6f, 1.0f),
            OrbDef(0.88f, 1.13f, 265f, 2, 0.55f, 0.9f),
            OrbDef(-0.12f, 0.12f, 25f, 2, 0.48f, 0.85f),
            OrbDef(-0.15f, 0.45f, 140f, 0, 0.55f, 0.95f),
            OrbDef(-0.12f, 0.78f, 260f, 1, 0.48f, 0.85f),
            OrbDef(-0.1f, 1.08f, 310f, 2, 0.45f, 0.8f),
            OrbDef(1.12f, 0.15f, 55f, 0, 0.48f, 0.85f),
            OrbDef(1.15f, 0.48f, 195f, 2, 0.55f, 0.95f),
            OrbDef(1.12f, 0.82f, 325f, 1, 0.48f, 0.85f),
            OrbDef(1.1f, -0.08f, 100f, 2, 0.45f, 0.8f),
            OrbDef(0.5f, -0.18f, 60f, 0, 0.5f, 0.7f),
            OrbDef(0.5f, 1.18f, 240f, 1, 0.5f, 0.7f)
        )

        Box(
            modifier = modifier.pointerInput(Unit) { }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val minDim = minOf(w, h)

                for (orb in orbs) {
                    val phase = phases[orb.phaseIndex]
                    val moveX = (phase - 0.5f) * 0.14f
                    val moveY = (phase - 0.5f) * 0.08f

                    val cx = w * (orb.xFrac + moveX)
                    val cy = h * (orb.yFrac + moveY)
                    val radius = minDim * orb.radiusFrac

                    val hue = (orb.baseHue + hueDrift + phase * 50f) % 360f
                    val intensity = orb.intensity * breathAlpha

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.hsv(hue = hue, saturation = 0.9f, value = 1f, alpha = 0.7f * intensity),
                                Color.hsv(hue = hue, saturation = 0.85f, value = 1f, alpha = 0.45f * intensity),
                                Color.hsv(hue = (hue + 20f) % 360f, saturation = 0.75f, value = 0.95f, alpha = 0.22f * intensity),
                                Color.hsv(hue = (hue + 40f) % 360f, saturation = 0.6f, value = 0.9f, alpha = 0.08f * intensity),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = radius
                        ),
                        center = Offset(cx, cy),
                        radius = radius
                    )
                }
            }
        }
    }
}

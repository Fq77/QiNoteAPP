package com.qinoteapp.qinoteapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.ui.theme.QiAnimation
import com.qinoteapp.qinoteapp.ui.theme.QiDuration
import com.qinoteapp.qinoteapp.ui.theme.QiEasing
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import java.text.DecimalFormat

@Composable
fun HeroEntrance(
    visible: Boolean,
    delayMs: Int = 0,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(QiDuration.Slow, delayMillis = delayMs, easing = QiEasing.EaseOut)
        ) + fadeIn(
            animationSpec = tween(QiDuration.Slow, delayMillis = delayMs, easing = QiEasing.EaseOut)
        ) + scaleIn(
            initialScale = 0.92f,
            animationSpec = tween(QiDuration.Slow, delayMillis = delayMs, easing = QiEasing.EaseOut)
        )
    ) {
        content()
    }
}

@Composable
fun StaggeredItem(
    index: Int,
    content: @Composable () -> Unit
) {
    val staggerDelay = index * QiAnimation.ListStaggerMs
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(QiDuration.Normal, delayMillis = staggerDelay, easing = QiEasing.EaseOut)
        ) + fadeIn(
            animationSpec = tween(QiDuration.Normal, delayMillis = staggerDelay, easing = QiEasing.EaseOut)
        )
    ) {
        content()
    }
}

fun Modifier.qiPressScale(scaleDown: Float = 0.96f): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = tween(QiDuration.Instant),
        label = "pressScale"
    )
    this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                isPressed = event.changes.any { it.pressed }
            }
        }
    }.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

@Composable
fun PressScale(
    scaleDown: Float = 0.96f,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = tween(QiDuration.Instant),
        label = "pressScale"
    )
    Box(
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.any { it.pressed }) {
                            isPressed = true
                        } else {
                            isPressed = false
                        }
                    }
                }
            }
    ) {
        content()
    }
}

@Composable
fun NumberCountUp(
    targetValue: Double,
    durationMs: Int = 800,
    prefix: String = "",
    suffix: String = "",
    decimals: Int = 2
): String {
    val animatable = remember { Animatable(0f) }
    LaunchedEffect(targetValue) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMs, easing = QiEasing.EaseOut)
        )
    }
    val format = remember(decimals) {
        DecimalFormat().apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
    }
    val currentValue = animatable.value.toDouble() * targetValue
    return "$prefix${format.format(currentValue)}$suffix"
}

@Composable
fun CardReveal(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(
            animationSpec = tween(QiDuration.Normal, easing = QiEasing.EaseOut)
        ) + fadeIn(
            animationSpec = tween(QiDuration.Normal, easing = QiEasing.EaseOut)
        ),
        exit = fadeOut(animationSpec = tween(QiDuration.Fast))
    ) {
        content()
    }
}

@Composable
fun PulseDot(
    color: Color = QiTheme.colors.Primary,
    size: androidx.compose.ui.unit.Dp = 6.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseDot")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(QiAnimation.PulseDurationMs, easing = QiEasing.EaseSmooth),
        ),
        label = "pulseScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(QiAnimation.PulseDurationMs, easing = QiEasing.EaseSmooth),
        ),
        label = "pulseAlpha"
    )
    Surface(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .graphicsLayer { this.alpha = alpha },
        shape = CircleShape,
        color = color
    ) {}
}

@Composable
fun FABEnter(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0f,
            animationSpec = spring(
                dampingRatio = 0.35f,
                stiffness = 300f
            )
        ) + fadeIn(
            animationSpec = tween(QiDuration.Normal, easing = QiEasing.EaseOut)
        )
    ) {
        content()
    }
}

@Composable
fun BubbleIn(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = 350f
            )
        ) + fadeIn(
            animationSpec = tween(QiDuration.Normal, easing = QiEasing.EaseOut)
        )
    ) {
        content()
    }
}

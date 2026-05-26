package com.qinoteapp.qinoteapp.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object QiDuration {
    const val Instant = 50
    const val Fast = 150
    const val Normal = 250
    const val Slow = 400
    const val Slower = 600
    const val Slowest = 800
}

object QiEasing {
    val EaseOut = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    val EaseSpring = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val EaseSmooth = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    val EaseDecelerate = CubicBezierEasing(0f, 0f, 0.2f, 1f)
}

object QiSpring {
    val Default = spring<Float>(dampingRatio = 0.75f, stiffness = 400f)
    val Bouncy = spring<Float>(dampingRatio = 0.35f, stiffness = 300f)
    val Gentle = spring<Float>(dampingRatio = 0.85f, stiffness = 200f)
}

object QiAnimation {
    const val HeroStaggerMs = 80
    const val ListStaggerMs = 50
    const val ShimmerDurationMs = 1500
    const val PulseDurationMs = 1200
    const val FloatingOrb1Ms = 20000
    const val FloatingOrb2Ms = 25000
}

inline fun <reified T> qiTween(durationMs: Int = QiDuration.Normal, easing: CubicBezierEasing = QiEasing.EaseOut): TweenSpec<T> = tween(durationMs, easing = easing)

package com.qinoteapp.qinoteapp.components

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.ui.theme.QiAnimation
import com.qinoteapp.qinoteapp.ui.theme.QiTheme

@Composable
fun MeshBackground(
    modifier: Modifier = Modifier,
    orbCount: Int = 2,
    orbSize: androidx.compose.ui.unit.Dp = 300.dp,
    alpha: Float = 0.06f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meshBg")

    val orb1X by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(QiAnimation.FloatingOrb1Ms)
        ),
        label = "orb1X"
    )
    val orb1Y by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(QiAnimation.FloatingOrb1Ms + 3000)
        ),
        label = "orb1Y"
    )

    val orb2X by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(QiAnimation.FloatingOrb2Ms)
        ),
        label = "orb2X"
    )
    val orb2Y by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(QiAnimation.FloatingOrb2Ms + 2000)
        ),
        label = "orb2Y"
    )

    Box(modifier = modifier.fillMaxSize()) {
        if (orbCount >= 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(orbSize)
                    .graphicsLayer {
                        translationX = orb1X
                        translationY = orb1Y
                        this.alpha = alpha
                    }
                    .blur(80.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                QiTheme.colors.PrimaryGlow,
                                QiTheme.colors.PrimaryGlow.copy(alpha = 0f)
                            )
                        )
                    )
            )
        }
        if (orbCount >= 2) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(orbSize)
                    .graphicsLayer {
                        translationX = orb2X
                        translationY = orb2Y
                        this.alpha = alpha
                    }
                    .blur(80.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                QiTheme.colors.Accent.copy(alpha = 0.3f),
                                QiTheme.colors.Accent.copy(alpha = 0f)
                            )
                        )
                    )
            )
        }
    }
}

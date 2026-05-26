package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val AiGradientColors = listOf(
    Color(0xFF6C5CE7),
    Color(0xFFA855F7),
    Color(0xFFEC4899),
    Color(0xFFF97316)
)

@Composable
fun AiIcon(
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    containerSize: Dp = 28.dp,
    shape: RoundedCornerShape = CircleShape
) {
    Box(
        modifier = modifier
            .size(containerSize)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(AiGradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI",
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}

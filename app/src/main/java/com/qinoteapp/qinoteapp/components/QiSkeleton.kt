package com.qinoteapp.qinoteapp.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
private fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Spacing.sm))
            .background(colors.Surface2.copy(alpha = alpha))
    )
}

@Composable
fun SummaryCardSkeleton(
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colors.Primary, colors.PrimaryLight)
                )
            )
            .padding(horizontal = Spacing.xxl, vertical = Spacing.xl)
    ) {
        Column {
            ShimmerBox(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            ShimmerBox(
                modifier = Modifier
                    .width(200.dp)
                    .height(44.dp)
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(Spacing.sm))
                        .background(colors.OnPrimary.copy(alpha = 0.15f))
                )
                ShimmerBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(Spacing.sm))
                        .background(colors.OnPrimary.copy(alpha = 0.15f))
                )
            }
        }
    }
}

@Composable
fun BillListSkeleton(
    count: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        repeat(count) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(14.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(12.dp)
                    )
                }
                ShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(16.dp)
                )
            }
        }
    }
}

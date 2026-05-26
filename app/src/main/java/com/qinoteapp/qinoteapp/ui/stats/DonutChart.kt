package com.qinoteapp.qinoteapp.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.data.entity.CategoryIcons
import com.qinoteapp.qinoteapp.ui.theme.QiSectionTitle
import com.qinoteapp.qinoteapp.ui.theme.QiDonutCenterAmount
import com.qinoteapp.qinoteapp.ui.theme.QiDonutCenterLabel
import com.qinoteapp.qinoteapp.ui.theme.QiElevation
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.QiEasing
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun DonutChart(
    categoryData: List<CategoryStat>,
    billType: Int,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = QiEasing.EaseOut),
        label = "donutProgress"
    )

    if (categoryData.isEmpty()) {
        return
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(QiRadius.xl),
        color = colors.Surface,
        shadowElevation = QiElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg)
        ) {
            Text(
                text = "分类构成",
                style = QiSectionTitle,
                color = colors.OnBackground
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                val isDark = isSystemInDarkTheme()
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val minDimension = size.minDimension
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = (minDimension * 0.35f)
                    val innerRadius = radius * 0.65f

                    var startAngle = 0f
                    val total = categoryData.sumOf { it.amount }

                    categoryData.forEach { stat ->
                        val angle = if (total == 0.0) 0f else (stat.amount / total).toFloat() * 360f * animatedProgress
                        val color = CategoryIcons.getCategoryColor(stat.categoryId, isDark)

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = angle,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                centerX - radius,
                                centerY - radius
                            ),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = 20.dp.toPx())
                        )

                        startAngle += angle
                    }

                    drawCircle(
                        color = colors.Surface,
                        radius = innerRadius,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val totalAmount = categoryData.sumOf { it.amount }
                    Text(
                        text = "¥${String.format("%.2f", totalAmount)}",
                        style = QiDonutCenterAmount,
                        color = colors.OnBackground
                    )
                    Text(
                        text = if (billType == 0) "总支出" else "总收入",
                        style = QiDonutCenterLabel,
                        color = colors.TextTertiary
                    )
                }
            }
        }
    }
}

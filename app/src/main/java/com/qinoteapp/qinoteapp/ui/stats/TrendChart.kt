package com.qinoteapp.qinoteapp.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiSectionTitle
import com.qinoteapp.qinoteapp.ui.theme.QiDuration
import com.qinoteapp.qinoteapp.ui.theme.QiElevation
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.abs

@Composable
fun TrendChart(
    trendData: List<TrendPoint>,
    billType: Int,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors

    if (trendData.isEmpty()) {
        return
    }

    val allZero = trendData.all {
        if (billType == 0) it.expense == 0.0 else it.income == 0.0
    }
    if (allZero) {
        return
    }

    val lineColor = if (billType == 0) colors.Expense else colors.Income
    val chartHeight = 280.dp

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(QiDuration.Slowest),
        label = "trendProgress"
    )

    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(QiRadius.xl),
        color = colors.Surface,
        shadowElevation = QiElevation.xs
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg)
        ) {
            Text(
                text = "趋势",
                style = QiSectionTitle,
                color = colors.OnBackground
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            val textMeasurer = rememberTextMeasurer()
            val labelStyle = remember {
                androidx.compose.ui.text.TextStyle(
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 9.sp,
                    color = colors.TextTertiary
                )
            }
            val yLabelStyle = remember {
                androidx.compose.ui.text.TextStyle(
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 9.sp,
                    color = colors.TextQuaternary
                )
            }
            val tooltipStyle = remember {
                androidx.compose.ui.text.TextStyle(
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 10.sp,
                    color = colors.OnBackground
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .wrapContentSize()
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(trendData) {
                            detectTapGestures { offset ->
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val yAxisWidth = 40f
                                val xLabelHeight = 36f
                                val chartTop = 8f
                                val chartBottom = canvasHeight - xLabelHeight
                                val chartHeightPx = chartBottom - chartTop
                                val chartLeft = yAxisWidth
                                val chartWidth = canvasWidth - yAxisWidth

                                val values = trendData.map {
                                    if (billType == 0) it.expense else it.income
                                }
                                val maxValue = values.maxOrNull()?.let { if (it == 0.0) 1.0 else it } ?: 1.0

                                val stepX = if (trendData.size > 1) chartWidth / (trendData.size - 1) else chartWidth / 2f

                                val points = values.mapIndexed { index, value ->
                                    Offset(
                                        x = chartLeft + if (trendData.size > 1) index * stepX else chartWidth / 2f,
                                        y = chartBottom - (value / maxValue).toFloat() * chartHeightPx * 0.9f
                                    )
                                }

                                val threshold = 30f
                                var closestIndex: Int? = null
                                var closestDist = Float.MAX_VALUE

                                points.forEachIndexed { index, point ->
                                    val dist = abs(offset.x - point.x) + abs(offset.y - point.y)
                                    if (dist < threshold && dist < closestDist) {
                                        closestDist = dist
                                        closestIndex = index
                                    }
                                }

                                selectedPointIndex = if (closestIndex != null) closestIndex else null
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val yAxisWidth = 40f
                    val xLabelHeight = 36f
                    val chartTop = 8f
                    val chartBottom = canvasHeight - xLabelHeight
                    val chartHeightPx = chartBottom - chartTop
                    val chartLeft = yAxisWidth
                    val chartWidth = canvasWidth - yAxisWidth

                    val values = trendData.map {
                        if (billType == 0) it.expense else it.income
                    }
                    val maxValue = values.maxOrNull()?.let { if (it == 0.0) 1.0 else it } ?: 1.0

                    for (i in 0..4) {
                        val y = chartTop + chartHeightPx * i / 4f
                        drawLine(
                            color = colors.Border,
                            start = Offset(chartLeft, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f
                        )
                        val yValue = maxValue * (4 - i) / 4.0
                        val yText = if (yValue >= 1000) "${String.format("%.0f", yValue / 1000)}k" else String.format("%.0f", yValue)
                        val yTextWidth = textMeasurer.measure(yText, yLabelStyle).size.width.toFloat()
                        drawText(
                            textMeasurer = textMeasurer,
                            text = yText,
                            topLeft = Offset(yAxisWidth - yTextWidth - 6f, y - 6f),
                            style = yLabelStyle
                        )
                    }

                    val stepX = if (trendData.size > 1) chartWidth / (trendData.size - 1) else chartWidth / 2f

                    val points = values.mapIndexed { index, value ->
                        Offset(
                            x = chartLeft + if (trendData.size > 1) index * stepX else chartWidth / 2f,
                            y = chartBottom - (value / maxValue).toFloat() * chartHeightPx * 0.9f
                        )
                    }

                    if (points.size > 1) {
                        val linePath = buildBezierCurvePath(points, chartTop, chartBottom)

                        val areaPath = Path().apply {
                            addPath(linePath)
                            lineTo(points.last().x, chartBottom)
                            lineTo(points.first().x, chartBottom)
                            close()
                        }

                        clipPath(areaPath) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor.copy(alpha = 0.3f),
                                        lineColor.copy(alpha = 0.02f)
                                    ),
                                    startY = chartTop,
                                    endY = chartBottom
                                ),
                                alpha = animatedProgress
                            )
                        }

                        drawPath(
                            path = linePath,
                            color = lineColor,
                            style = Stroke(width = 2.dp.toPx()),
                            alpha = animatedProgress
                        )

                        points.forEach { point ->
                            drawCircle(
                                color = lineColor,
                                radius = 3.dp.toPx(),
                                center = point,
                                alpha = animatedProgress
                            )
                        }
                    } else if (points.size == 1) {
                        drawCircle(
                            color = lineColor,
                            radius = 4.dp.toPx(),
                            center = points.first(),
                            alpha = animatedProgress
                        )
                    }

                    val labelInterval = if (trendData.size <= 7) 1 else trendData.size / 6
                    trendData.forEachIndexed { index, point ->
                        if (index % labelInterval == 0 || index == trendData.size - 1) {
                            val x = chartLeft + if (trendData.size > 1) index * stepX else chartWidth / 2f
                            val textWidth = textMeasurer.measure(point.date, labelStyle).size.width.toFloat()
                            val labelX = when {
                                index == 0 -> maxOf(yAxisWidth, x - textWidth / 2f).coerceAtMost(canvasWidth - textWidth)
                                index == trendData.size - 1 -> minOf(canvasWidth - textWidth, x - textWidth / 2f).coerceAtLeast(yAxisWidth)
                                else -> (x - textWidth / 2f).coerceIn(yAxisWidth, canvasWidth - textWidth)
                            }
                            drawText(
                                textMeasurer = textMeasurer,
                                text = point.date,
                                topLeft = Offset(labelX, chartBottom + 6f),
                                style = labelStyle
                            )
                        }
                    }

                    selectedPointIndex?.let { idx ->
                        if (idx in points.indices) {
                            val point = points[idx]
                            val dataPoint = trendData[idx]
                            val value = if (billType == 0) dataPoint.expense else dataPoint.income
                            val dateText = dataPoint.date
                            val valueText = String.format("%.2f", value)

                            val dateTextLayout = textMeasurer.measure(dateText, tooltipStyle)
                            val valueTextLayout = textMeasurer.measure(valueText, tooltipStyle)

                            val tooltipPaddingH = 10f
                            val tooltipPaddingV = 6f
                            val tooltipGap = 4f
                            val lineSpacing = 2f

                            val tooltipWidth = maxOf(dateTextLayout.size.width, valueTextLayout.size.width).toFloat() + tooltipPaddingH * 2
                            val tooltipHeight = dateTextLayout.size.height.toFloat() + valueTextLayout.size.height.toFloat() + tooltipPaddingV * 2 + lineSpacing

                            val tooltipX = (point.x - tooltipWidth / 2f).coerceIn(yAxisWidth, canvasWidth - tooltipWidth)
                            val tooltipY = if (point.y - tooltipHeight - tooltipGap - 8f >= chartTop) {
                                point.y - tooltipHeight - tooltipGap - 8f
                            } else {
                                point.y + tooltipGap + 8f
                            }

                            drawRoundRect(
                                color = colors.Surface,
                                topLeft = Offset(tooltipX, tooltipY),
                                size = androidx.compose.ui.geometry.Size(tooltipWidth, tooltipHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                                alpha = 0.95f
                            )
                            drawRoundRect(
                                color = lineColor,
                                topLeft = Offset(tooltipX, tooltipY),
                                size = androidx.compose.ui.geometry.Size(tooltipWidth, tooltipHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                                style = Stroke(width = 1.5f),
                                alpha = 0.6f
                            )

                            drawText(
                                textMeasurer = textMeasurer,
                                text = dateText,
                                topLeft = Offset(
                                    tooltipX + (tooltipWidth - dateTextLayout.size.width) / 2f,
                                    tooltipY + tooltipPaddingV
                                ),
                                style = tooltipStyle
                            )
                            drawText(
                                textMeasurer = textMeasurer,
                                text = valueText,
                                topLeft = Offset(
                                    tooltipX + (tooltipWidth - valueTextLayout.size.width) / 2f,
                                    tooltipY + tooltipPaddingV + dateTextLayout.size.height.toFloat() + lineSpacing
                                ),
                                style = tooltipStyle.copy(color = lineColor)
                            )

                            drawCircle(
                                color = colors.Surface,
                                radius = 6.dp.toPx(),
                                center = point,
                                alpha = animatedProgress
                            )
                            drawCircle(
                                color = lineColor,
                                radius = 4.dp.toPx(),
                                center = point,
                                alpha = animatedProgress
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildBezierCurvePath(
    points: List<Offset>,
    chartTop: Float,
    chartBottom: Float,
    tension: Float = 0.3f
): Path = Path().apply {
    if (points.isEmpty()) return Path()
    moveTo(points.first().x, points.first().y)
    if (points.size > 2) {
        for (i in 1 until points.size - 1) {
            val prev = points[i - 1]
            val curr = points[i]
            val next = points[i + 1]
            val cp1x = curr.x - (next.x - prev.x) * tension
            val cp1y = (curr.y - (next.y - prev.y) * tension).coerceIn(chartTop, chartBottom)
            if (i == 1) {
                cubicTo(
                    cp1x, (points[0].y + (curr.y - points[0].y) * tension).coerceIn(chartTop, chartBottom),
                    cp1x, cp1y,
                    curr.x, curr.y
                )
            } else {
                val prevPrev = if (i > 1) points[i - 2] else prev
                cubicTo(
                    (prev.x + (curr.x - prevPrev.x) * tension),
                    (prev.y + (curr.y - prevPrev.y) * tension).coerceIn(chartTop, chartBottom),
                    cp1x, cp1y,
                    curr.x, curr.y
                )
            }
        }
        val last = points.last()
        val secondLast = points[points.size - 2]
        cubicTo(
            secondLast.x + (last.x - secondLast.x) * tension,
            (secondLast.y + (last.y - secondLast.y) * tension).coerceIn(chartTop, chartBottom),
            last.x - (last.x - secondLast.x) * tension * 0.5f,
            last.y.coerceIn(chartTop, chartBottom),
            last.x, last.y
        )
    } else if (points.size == 2) {
        lineTo(points[1].x, points[1].y)
    }
}

package com.qinoteapp.qinoteapp.ui.home

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.components.NumberCountUp
import com.qinoteapp.qinoteapp.components.PulseDot
import com.qinoteapp.qinoteapp.ui.theme.QiCardLabel
import com.qinoteapp.qinoteapp.ui.theme.QiCardLabelSmall
import com.qinoteapp.qinoteapp.ui.theme.QiAmountLarge
import com.qinoteapp.qinoteapp.ui.theme.QiAmountSign
import com.qinoteapp.qinoteapp.ui.theme.QiAmountMedium
import com.qinoteapp.qinoteapp.ui.theme.QiAmountSmall
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import com.qinoteapp.qinoteapp.util.AmountUtils

@Composable
fun SummaryCard(
    todayExpense: Long,
    todayIncome: Long,
    monthExpense: Long = 0L,
    monthIncome: Long = 0L,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors

    val infiniteTransition = rememberInfiniteTransition(label = "summaryCard")
    val orb1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
        ),
        label = "orb1Scale"
    )
    val orb2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            initialStartOffset = StartOffset(1500)
        ),
        label = "orb2Scale"
    )
    val orb1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOut),
        ),
        label = "orb1Alpha"
    )
    val orb2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = EaseInOut),
            initialStartOffset = StartOffset(2000)
        ),
        label = "orb2Alpha"
    )

    val balance = todayIncome - todayExpense
    val isSurplus = balance >= 0
    val absBalance = kotlin.math.abs(balance)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = colors.PrimaryGlow,
                spotColor = colors.PrimaryGlow
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.Primary, colors.PrimaryLight),
                        start = Offset(0f, 0f),
                        end = Offset(1f, 1f)
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val stripeSpacing = 40f
                var x = -size.height
                while (x < size.width + size.height) {
                    drawLine(
                        color = colors.OnPrimary.copy(alpha = 0.06f),
                        start = Offset(x, 0f),
                        end = Offset(x + size.height, size.height),
                        strokeWidth = 1.2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                    )
                    x += stripeSpacing
                }
            }

            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = colors.OnPrimary.copy(alpha = orb1Alpha),
                    radius = (size.minDimension * 0.3f) * orb1Scale,
                    center = Offset(size.width * 0.82f, size.height * 0.12f)
                )
                drawCircle(
                    color = colors.OnPrimary.copy(alpha = orb2Alpha),
                    radius = (size.minDimension * 0.24f) * orb2Scale,
                    center = Offset(size.width * 0.18f, size.height * 0.85f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xxl, vertical = Spacing.xl)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        text = "今日概览",
                        style = QiCardLabel,
                        color = colors.OnPrimary.copy(alpha = 0.9f)
                    )
                    PulseDot(color = colors.OnPrimary, size = 6.dp)
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = if (isSurplus) "+" else "-",
                        style = QiAmountSign,
                        color = if (isSurplus) colors.OnPrimary else colors.OnPrimarySoft
                    )
                    Text(
                        text = NumberCountUp(
                            targetValue = AmountUtils.centsToYuan(absBalance),
                            prefix = "¥",
                            decimals = 2
                        ),
                        style = QiAmountLarge,
                        color = if (isSurplus) colors.OnPrimary else colors.OnPrimaryMuted
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = colors.OnPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(Spacing.sm)
                            )
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(colors.Expense, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "支出",
                            style = QiCardLabelSmall,
                            color = colors.OnPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = NumberCountUp(
                                targetValue = AmountUtils.centsToYuan(todayExpense),
                                prefix = "¥",
                                decimals = 2
                            ),
                            style = QiAmountMedium,
                            color = colors.OnPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = colors.OnPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(Spacing.sm)
                            )
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(colors.Income, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "收入",
                            style = QiCardLabelSmall,
                            color = colors.OnPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = NumberCountUp(
                                targetValue = AmountUtils.centsToYuan(todayIncome),
                                prefix = "¥",
                                decimals = 2
                            ),
                            style = QiAmountMedium,
                            color = colors.OnPrimary
                        )
                    }
                }

                if (monthExpense > 0 || monthIncome > 0) {
                    Spacer(modifier = Modifier.height(Spacing.md))

                    HorizontalDivider(
                        color = colors.OnPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.padding(vertical = Spacing.xs)
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "本月",
                            style = QiCardLabelSmall,
                            color = colors.OnPrimary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .background(colors.Expense, RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(Spacing.xs))
                                Text(
                                    text = NumberCountUp(
                                        targetValue = AmountUtils.centsToYuan(monthExpense),
                                        prefix = "¥",
                                        decimals = 2
                                    ),
                                    style = QiAmountSmall,
                                    color = colors.OnPrimary.copy(alpha = 0.85f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .background(colors.Income, RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(Spacing.xs))
                                Text(
                                    text = NumberCountUp(
                                        targetValue = AmountUtils.centsToYuan(monthIncome),
                                        prefix = "¥",
                                        decimals = 2
                                    ),
                                    style = QiAmountSmall,
                                    color = colors.OnPrimary.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

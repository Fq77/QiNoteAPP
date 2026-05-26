package com.qinoteapp.qinoteapp.ui.stats

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.components.StaggeredItem
import com.qinoteapp.qinoteapp.data.entity.CategoryIcons
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.QiEasing
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun CategoryList(
    categoryData: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        categoryData.forEachIndexed { index, stat ->
            StaggeredItem(index = index) {
                CategoryListItem(
                    stat = stat,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun CategoryListItem(
    stat: CategoryStat,
    colors: com.qinoteapp.qinoteapp.ui.theme.QiColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CategoryIcons.getIcon(stat.categoryIcon),
            contentDescription = null,
            tint = colors.OnBackground,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.OnBackground,
                    maxLines = 1
                )

                Text(
                    text = "¥${String.format("%.0f", stat.amount)}",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 11.sp,
                    color = colors.OnBackground,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val animatedPercentage by animateFloatAsState(
                    targetValue = stat.percentage / 100f,
                    animationSpec = tween(600, easing = QiEasing.EaseOut),
                    label = "catProgress_${stat.categoryId}"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(QiRadius.full))
                        .background(colors.Surface2)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedPercentage)
                            .height(4.dp)
                            .clip(RoundedCornerShape(QiRadius.full))
                            .background(CategoryIcons.getCategoryColor(stat.categoryId))
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.xs))

                Text(
                    text = "${String.format("%.0f", stat.percentage)}%",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 10.sp,
                    color = colors.TextTertiary
                )
            }
        }
    }
}

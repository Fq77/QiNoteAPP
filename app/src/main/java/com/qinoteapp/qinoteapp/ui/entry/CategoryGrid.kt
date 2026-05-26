package com.qinoteapp.qinoteapp.ui.entry

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryIcons
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryGrid(
    categories: List<CategoryEntity>,
    selectedId: String,
    onSelect: (String) -> Unit,
    filterType: String = "expense"
) {
    val colors = QiTheme.colors
    val filtered = categories.filter { it.type == filterType }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        filtered.forEach { category ->
            val isSelected = category.id == selectedId
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = tween(200),
                label = "catScale_${category.id}"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(category.id) }
                    )
            ) {
                val categoryColor = CategoryIcons.getCategoryColor(category.id)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.12f))
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, categoryColor, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    if (category.icon == "custom") {
                        Text(
                            text = category.name.take(1),
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            color = categoryColor,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Icon(
                            imageVector = CategoryIcons.getIcon(category.icon),
                            contentDescription = category.name,
                            tint = categoryColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = category.name,
                    fontFamily = JakartaFontFamily,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 11.sp,
                    color = if (isSelected) colors.OnBackground else colors.OnSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

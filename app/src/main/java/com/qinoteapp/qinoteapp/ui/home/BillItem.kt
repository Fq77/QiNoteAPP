package com.qinoteapp.qinoteapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.components.DeleteConfirmSheet
import com.qinoteapp.qinoteapp.components.qiPressScale
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryIcons
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import com.qinoteapp.qinoteapp.util.AmountUtils

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BillItem(
    bill: BillEntity,
    categoryInfo: Pair<String, String>?,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors
    val isExpense = bill.type == "expense"
    val categoryName = categoryInfo?.first ?: bill.category
    val categoryIcon = categoryInfo?.second
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                showDeleteConfirm = true
                false // Don't dismiss yet, wait for confirmation
            } else {
                false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.5f }
    )

    if (showDeleteConfirm) {
        DeleteConfirmSheet(
            onDismiss = { showDeleteConfirm = false },
            onConfirm = { onDelete() }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val alignment = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        colors.Expense.copy(alpha = 0.15f),
                        RoundedCornerShape(QiRadius.lg)
                    )
                    .padding(end = Spacing.xxl),
                contentAlignment = alignment
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "删除",
                        tint = colors.Expense,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "删除",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.Expense
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .qiPressScale()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(QiRadius.lg),
            color = colors.Surface,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Box(
                    modifier = Modifier.offset(x = (-4).dp)
                ) {
                    val categoryColor = CategoryIcons.getCategoryColor(bill.category, isSystemInDarkTheme())
                    Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIcons.getIcon(categoryIcon ?: ""),
                        contentDescription = categoryName,
                        tint = categoryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
                ) {
                    Text(
                        text = bill.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W500),
                        color = colors.OnBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (bill.time.isNotBlank()) {
                            Text(
                                text = bill.time,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.TextTertiary
                            )
                        }
                        if (bill.time.isNotBlank() && bill.note.isNotBlank()) {
                            Text(
                                text = "·",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.TextTertiary
                            )
                        }
                        if (bill.note.isNotBlank()) {
                            Text(
                                text = bill.note,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }

                Text(
                    text = AmountUtils.formatCentsWithSign(bill.amount, isExpense),
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = OutfitFontFamily, fontSize = 15.sp),
                    color = if (isExpense) colors.Expense else colors.Income
                )
            }
        }
    }
}

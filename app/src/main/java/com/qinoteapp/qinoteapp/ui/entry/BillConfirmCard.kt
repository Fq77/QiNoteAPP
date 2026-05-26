package com.qinoteapp.qinoteapp.ui.entry

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.components.QiButton
import com.qinoteapp.qinoteapp.data.entity.CategoryIcons
import com.qinoteapp.qinoteapp.network.ParsedBill
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import com.qinoteapp.qinoteapp.util.AmountUtils

@Composable
fun BillConfirmCard(
    parsedBill: ParsedBill,
    billConfirmed: Boolean,
    onConfirm: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors
    val isExpense = parsedBill.type == "expense"
    val categoryColor = CategoryIcons.getCategoryColor(parsedBill.category)
    val categoryIcon = CategoryIcons.getIconForCategory(parsedBill.category)
    val amountColor = if (isExpense) colors.Expense else colors.Income
    val categoryName = getCategoryDisplayName(parsedBill.category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(QiRadius.lg))
            .background(colors.Surface1)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(QiRadius.md))
                            .background(categoryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = categoryName,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = categoryName,
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.W500,
                        fontSize = 13.sp,
                        color = colors.OnSurface
                    )
                }

                Text(
                    text = AmountUtils.formatCentsWithSign(parsedBill.amount, isExpense),
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W700,
                    fontSize = 20.sp,
                    color = amountColor
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Text(
                text = parsedBill.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.OnBackground
            )

            if (parsedBill.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = parsedBill.note,
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 13.sp,
                    color = colors.TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                if (parsedBill.date.isNotBlank()) {
                    Text(
                        text = parsedBill.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.TextTertiary
                    )
                }
                if (parsedBill.time.isNotBlank()) {
                    Text(
                        text = parsedBill.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.TextTertiary
                    )
                }
            }

            if (!billConfirmed) {
                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    QiButton.Primary(
                        text = "确认记账",
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )

                    QiButton.Secondary(
                        text = "修改",
                        onClick = onEdit,
                        icon = Icons.Default.Edit
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "已记录",
                            tint = colors.Income,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = "已记录",
                            fontFamily = JakartaFontFamily,
                            fontWeight = FontWeight.W500,
                            fontSize = 13.sp,
                            color = colors.Income
                        )
                    }

                    QiButton.Secondary(
                        text = "修改",
                        onClick = onEdit,
                        icon = Icons.Default.Edit
                    )
                }
            }
        }
    }
}

private val categoryDisplayNames = mapOf(
    "food" to "餐饮美食", "transport" to "交通出行", "shopping" to "购物消费",
    "entertainment" to "休闲娱乐", "living" to "居住生活", "daily" to "日常杂项",
    "health" to "医疗健康", "education" to "教育培训", "social" to "人情社交",
    "pet" to "宠物萌宠", "invest_expense" to "投资理财", "digital" to "数码电子",
    "housing" to "住房缴费", "salary" to "工资收入", "freelance" to "兼职收入",
    "investment" to "投资收益"
)

fun getCategoryDisplayName(categoryId: String): String {
    return categoryDisplayNames[categoryId] ?: categoryId
}

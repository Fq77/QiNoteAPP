package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.network.ParsedBill
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiColorScheme
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import com.qinoteapp.qinoteapp.util.AmountUtils

@Composable
private fun ChangedFieldRow(
    label: String,
    oldValue: String,
    newValue: String,
    colors: QiColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(QiRadius.sm))
            .background(colors.PrimaryLighter)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 13.sp,
            color = colors.Primary,
            modifier = Modifier.width(48.dp)
        )
        Text(
            text = oldValue,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 13.sp,
            color = colors.TextTertiary,
            textDecoration = TextDecoration.LineThrough
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = "→",
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 13.sp,
            color = colors.Primary
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = newValue,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 13.sp,
            color = colors.Primary
        )
    }
}

@Composable
private fun UnchangedFieldRow(
    label: String,
    value: String,
    colors: QiColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 13.sp,
            color = colors.TextTertiary,
            modifier = Modifier.width(48.dp)
        )
        Text(
            text = value,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 13.sp,
            color = colors.OnSurface
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BillCompareDialog(
    originalBill: BillEntity,
    newParsedBill: ParsedBill,
    categoryNames: Map<String, Pair<String, String>>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = QiTheme.colors
    val oldCategoryDisplay = categoryNames[originalBill.category]?.first ?: originalBill.category
    val newCategoryDisplay = categoryNames[newParsedBill.category]?.first ?: newParsedBill.category

    val fields = listOf(
        Triple("标题", originalBill.title, newParsedBill.title),
        Triple("金额", "¥${AmountUtils.formatCents(originalBill.amount)}", "¥${AmountUtils.formatCents(newParsedBill.amount)}"),
        Triple("分类", oldCategoryDisplay, newCategoryDisplay),
        Triple("日期", originalBill.date, newParsedBill.date),
        Triple("时间", originalBill.time, newParsedBill.time),
        Triple("备注", originalBill.note, newParsedBill.note)
    )

    QiSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxl)
                .padding(bottom = Spacing.xxxl)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    tint = colors.Primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "分析结果对比",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = colors.OnBackground
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            for ((label, oldValue, newValue) in fields) {
                if (oldValue != newValue) {
                    ChangedFieldRow(label = label, oldValue = oldValue, newValue = newValue, colors = colors)
                } else {
                    UnchangedFieldRow(label = label, value = oldValue, colors = colors)
                }
                Spacer(modifier = Modifier.height(Spacing.xs))
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                QiButton.Secondary(
                    text = "放弃",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                QiButton.Primary(
                    text = "确认更新",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

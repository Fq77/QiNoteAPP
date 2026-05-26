package com.qinoteapp.qinoteapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.components.StaggeredItem
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import com.qinoteapp.qinoteapp.util.AmountUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DateBillGroup(
    val date: String,
    val bills: List<BillEntity>,
    val dayExpense: Long,
    val dayIncome: Long
)

@Composable
fun BillDateGroup(
    group: DateBillGroup,
    categories: Map<String, Pair<String, String>>,
    onBillClick: (BillEntity) -> Unit,
    onDeleteBill: (BillEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors
    val formattedDate = formatChineseDate(group.date)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleSmall,
                color = colors.OnBackground
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "支出 ¥${AmountUtils.formatCents(group.dayExpense)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.Expense
                )
                Text(
                    text = "收入 ¥${AmountUtils.formatCents(group.dayIncome)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.Income
                )
            }
        }

        group.bills.forEachIndexed { index, bill ->
            StaggeredItem(index = index) {
                BillItem(
                    bill = bill,
                    categoryInfo = categories[bill.category],
                    onClick = { onBillClick(bill) },
                    onDelete = { onDeleteBill(bill) }
                )
            }
        }
    }
}

private fun formatChineseDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        val month = date.monthValue
        val day = date.dayOfMonth
        val dayOfWeek = when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "周一"
            DayOfWeek.TUESDAY -> "周二"
            DayOfWeek.WEDNESDAY -> "周三"
            DayOfWeek.THURSDAY -> "周四"
            DayOfWeek.FRIDAY -> "周五"
            DayOfWeek.SATURDAY -> "周六"
            DayOfWeek.SUNDAY -> "周日"
            else -> ""
        }
        "${month}月${day}日 $dayOfWeek"
    } catch (e: Exception) {
        dateStr
    }
}

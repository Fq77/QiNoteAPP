package com.qinoteapp.qinoteapp.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qinoteapp.qinoteapp.components.HeroEntrance
import com.qinoteapp.qinoteapp.components.NumberCountUp
import com.qinoteapp.qinoteapp.components.QiStatCard
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun QuickStatsRow(
    totalExpense: Double,
    totalIncome: Double,
    dailyAverage: Double,
    comparedToLast: Double,
    balance: Double,
    billType: Int
) {
    val colors = QiTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        HeroEntrance(visible = true, delayMs = 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                QiStatCard(
                    title = if (billType == 0) "总支出" else "总收入",
                    value = NumberCountUp(targetValue = if (billType == 0) totalExpense else totalIncome, prefix = if (billType == 0) "-¥" else "+¥"),
                    valueColor = if (billType == 0) colors.Expense else colors.Income,
                    weight = 1f
                )
                QiStatCard(
                    title = if (billType == 0) "日均支出" else "日均收入",
                    value = NumberCountUp(targetValue = dailyAverage, prefix = "¥"),
                    weight = 1f
                )
            }
        }

        HeroEntrance(visible = true, delayMs = 60) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                QiStatCard(
                    title = if (billType == 0) "较上期支出" else "较上期收入",
                    value = "${if (comparedToLast >= 0) "+" else ""}${String.format("%.1f", comparedToLast)}%",
                    valueColor = if (comparedToLast > 0) colors.Expense else colors.Income,
                    weight = 1f
                )
                QiStatCard(
                    title = if (balance >= 0) "收支结余" else "收支亏损",
                    value = NumberCountUp(targetValue = kotlin.math.abs(balance), prefix = if (balance >= 0) "+¥" else "-¥"),
                    valueColor = if (balance >= 0) colors.Income else colors.Expense,
                    weight = 1f
                )
            }
        }
    }
}

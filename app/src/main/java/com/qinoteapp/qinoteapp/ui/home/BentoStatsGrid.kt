package com.qinoteapp.qinoteapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.qinoteapp.qinoteapp.components.HeroEntrance
import com.qinoteapp.qinoteapp.components.QiStatCard
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import java.text.DecimalFormat

@Composable
fun BentoStatsGrid(
    monthExpense: Double,
    monthIncome: Double,
    billCount: Int,
    monthBalance: Double,
    modifier: Modifier = Modifier
) {
    val decimalFormat = remember { DecimalFormat("#,##0.00") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            HeroEntrance(visible = true, delayMs = 0) {
                QiStatCard(
                    title = "月支出",
                    value = "¥${decimalFormat.format(monthExpense)}",
                    weight = 2f
                )
            }
            HeroEntrance(visible = true, delayMs = 80) {
                QiStatCard(
                    title = "账单数",
                    value = "$billCount",
                    weight = 1f
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            HeroEntrance(visible = true, delayMs = 160) {
                QiStatCard(
                    title = "月收入",
                    value = "¥${decimalFormat.format(monthIncome)}",
                    weight = 1f
                )
            }
            HeroEntrance(visible = true, delayMs = 240) {
                QiStatCard(
                    title = "月结余",
                    value = "¥${decimalFormat.format(monthBalance)}",
                    weight = 2f
                )
            }
        }
    }
}

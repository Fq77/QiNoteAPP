package com.qinoteapp.qinoteapp.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.qinoteapp.qinoteapp.components.HeroEntrance
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.QiEmptyState
import com.qinoteapp.qinoteapp.components.SegmentedControl
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.ui.theme.QiPeriodLabel
import com.qinoteapp.qinoteapp.ui.theme.QiSectionTitle
import com.qinoteapp.qinoteapp.ui.theme.QiElevation
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun StatsScreen(
    navController: NavHostController,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = QiTheme.colors
    var showPeriodPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.Background,
        topBar = {
            TopNavBar(mode = NavBarMode.StatsMode(title = "统计"))
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.Background)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(start = Spacing.lg, top = Spacing.sm, end = Spacing.lg, bottom = Spacing.xxl)
        ) {
            item {
                HeroEntrance(visible = true, delayMs = 0) {
                    TimeFilterBar(
                        selectedIndex = uiState.periodType,
                        onSelected = { viewModel.setPeriodType(it) }
                    )
                }
            }

            item {
                HeroEntrance(visible = true, delayMs = 80) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { viewModel.navigatePeriod(-1) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "上一期",
                                    tint = colors.OnSurface,
                                    modifier = Modifier.width(24.dp)
                                )
                            }

                            Text(
                                text = uiState.currentPeriod.label,
                                style = QiPeriodLabel,
                                color = colors.OnBackground,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { showPeriodPicker = true }
                                    )
                                    .background(
                                        colors.Surface1,
                                        RoundedCornerShape(QiRadius.md)
                                    )
                                    .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                            )

                            IconButton(
                                onClick = { viewModel.navigatePeriod(1) },
                                enabled = !uiState.isAtCurrentPeriod
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "下一期",
                                    tint = if (uiState.isAtCurrentPeriod) colors.TextQuaternary else colors.OnSurface,
                                    modifier = Modifier.width(24.dp)
                                )
                            }
                        }

                        SegmentedControl(
                            segments = listOf("支出", "收入"),
                            selectedIndex = uiState.billType,
                            onSegmentSelected = { viewModel.setBillType(it) },
                            activeColor = if (uiState.billType == 0) colors.Expense else colors.Income,
                            modifier = Modifier
                                .width(160.dp)
                        )
                    }
                }
            }

            item {
                HeroEntrance(visible = true, delayMs = 160) {
                    QuickStatsRow(
                        totalExpense = uiState.statsData.totalExpense,
                        totalIncome = uiState.statsData.totalIncome,
                        dailyAverage = uiState.statsData.dailyAverage,
                        comparedToLast = uiState.statsData.comparedToLast,
                        balance = uiState.statsData.balance,
                        billType = uiState.billType
                    )
                }
            }

            item {
                HeroEntrance(visible = true, delayMs = 240) {
                    val trendHasData = uiState.statsData.trendData.isNotEmpty() &&
                        uiState.statsData.trendData.any { if (uiState.billType == 0) it.expense > 0.0 else it.income > 0.0 }

                    if (trendHasData) {
                        TrendChart(
                            trendData = uiState.statsData.trendData,
                            billType = uiState.billType
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
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
                                QiEmptyState(
                                    icon = Icons.AutoMirrored.Outlined.ShowChart,
                                    title = "暂无数据",
                                    modifier = Modifier.height(120.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                HeroEntrance(visible = true, delayMs = 320) {
                    if (uiState.statsData.categoryData.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                        ) {
                            DonutChart(
                                categoryData = uiState.statsData.categoryData,
                                billType = uiState.billType,
                                modifier = Modifier.fillMaxWidth()
                            )

                            CategoryList(
                                categoryData = uiState.statsData.categoryData,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(QiRadius.xl),
                            color = colors.Surface,
                            shadowElevation = QiElevation.xs
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.lg)
                            ) {
                                Text(
                                    text = "分类构成",
                                    style = QiSectionTitle,
                                    color = colors.OnBackground
                                )
                                QiEmptyState(
                                    icon = Icons.AutoMirrored.Outlined.ShowChart,
                                    title = "暂无数据",
                                    modifier = Modifier.height(120.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    PeriodPickerSheet(
        visible = showPeriodPicker,
        onDismiss = { showPeriodPicker = false },
        onConfirm = { start, end ->
            viewModel.setCustomPeriod(start, end)
            showPeriodPicker = false
        },
        currentStart = uiState.currentPeriod.start,
        currentEnd = uiState.currentPeriod.end,
        periodType = uiState.periodType
    )
}

package com.qinoteapp.qinoteapp.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import com.qinoteapp.qinoteapp.components.BillListSkeleton
import com.qinoteapp.qinoteapp.components.MeshBackground
import com.qinoteapp.qinoteapp.components.NavBarMode
import com.qinoteapp.qinoteapp.components.NoiseOverlay
import com.qinoteapp.qinoteapp.components.QiEmptyState
import com.qinoteapp.qinoteapp.components.QiTextField
import com.qinoteapp.qinoteapp.components.QiToastHost
import com.qinoteapp.qinoteapp.components.SummaryCardSkeleton
import com.qinoteapp.qinoteapp.components.TopNavBar
import com.qinoteapp.qinoteapp.navigation.QiRoute
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onFabClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val vmToast by viewModel.toastMessage.collectAsState()
    val colors = QiTheme.colors

    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.Background)
    ) {
        MeshBackground()

        NoiseOverlay()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopNavBar(
                mode = NavBarMode.HomeMode(
                    title = "奇记",
                    onSettingsClick = { navController.navigate(QiRoute.Settings.route) }
                )
            )

            AnimatedContent(
                targetState = uiState.isLoading && uiState.bills.isEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "homeContent",
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { showSkeleton ->
                if (showSkeleton) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        SummaryCardSkeleton()
                        Spacer(modifier = Modifier.height(Spacing.lg))
                        BillListSkeleton(count = 5)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.md,
                            bottom = Spacing.xxxl
                        )
                    ) {
                    item {
                        SummaryCard(
                            todayExpense = uiState.todayExpense,
                            todayIncome = uiState.todayIncome,
                            monthExpense = uiState.monthExpense,
                            monthIncome = uiState.monthIncome
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(16.dp)
                                    .background(
                                        colors.Primary,
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                            Text(
                                text = "账单明细",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.OnBackground
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { viewModel.toggleSearch() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "搜索",
                                    tint = if (uiState.isSearchExpanded) colors.Primary else colors.TextTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = uiState.isSearchExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            QiTextField(
                                value = uiState.searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                label = "搜索账单",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Spacing.xs),
                                prefix = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "搜索",
                                        tint = colors.TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true
                            )
                        }
                    }

                    if (uiState.bills.isEmpty()) {
                        item {
                            QiEmptyState(
                                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                                title = "暂无账单记录",
                                actionText = "记一笔",
                                onAction = onFabClick,
                                modifier = Modifier.height(200.dp)
                            )
                        }
                    } else {
                        val filteredBills = uiState.filteredBills

                        if (filteredBills.isEmpty() && uiState.searchQuery.trim().isNotBlank()) {
                            item {
                                QiEmptyState(
                                    icon = Icons.Default.Search,
                                    title = "未找到相关账单",
                                    subtitle = "尝试其他关键词搜索",
                                    modifier = Modifier.height(200.dp)
                                )
                            }
                        } else {
                            val groupedBills = filteredBills.groupBy { it.date }
                            val sortedGroups = groupedBills.toSortedMap(reverseOrder())

                            sortedGroups.entries.forEachIndexed { groupIndex, (date, bills) ->
                                val dayExpense = bills.filter { it.type == "expense" }.sumOf { it.amount }
                                val dayIncome = bills.filter { it.type == "income" }.sumOf { it.amount }
                                val group = DateBillGroup(date = date, bills = bills, dayExpense = dayExpense, dayIncome = dayIncome)
                                item(key = "group_$date") {
                                    BillDateGroup(
                                        group = group,
                                        categories = uiState.categories,
                                        onBillClick = {
                                            navController.navigate(QiRoute.BillDetail.createRoute(it.id))
                                        },
                                        onDeleteBill = { bill ->
                                            viewModel.deleteBill(bill)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                }
            }
        }

        QiToastHost(
            toast = vmToast,
            onDismiss = { viewModel.clearToast() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.xl)
        )

        if (showScrollToTop) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.Surface1)
                    .clickable {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "返回顶部",
                    tint = colors.OnSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

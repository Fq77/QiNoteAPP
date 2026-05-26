package com.qinoteapp.qinoteapp.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import com.qinoteapp.qinoteapp.data.repository.BillRepository
import com.qinoteapp.qinoteapp.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.qinoteapp.qinoteapp.util.AmountUtils
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class TimePeriod(
    val type: String,
    val start: String,
    val end: String,
    val label: String
)

data class TrendPoint(
    val date: String,
    val expense: Double,
    val income: Double
)

data class CategoryStat(
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String = "",
    val amount: Double,
    val count: Int,
    val percentage: Float
)

data class StatsData(
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val dailyAverage: Double = 0.0,
    val comparedToLast: Double = 0.0,
    val balance: Double = 0.0,
    val trendData: List<TrendPoint> = emptyList(),
    val categoryData: List<CategoryStat> = emptyList()
)

data class StatsUiState(
    val periodType: Int = 0,
    val currentPeriod: TimePeriod = computeCurrentWeekPeriod(),
    val billType: Int = 0,
    val statsData: StatsData = StatsData(),
    val categories: List<CategoryEntity> = emptyList(),
    val isAtCurrentPeriod: Boolean = true
)

private fun computeCurrentWeekPeriod(): TimePeriod {
    val now = LocalDate.now()
    val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    return TimePeriod(
        type = "week",
        start = startOfWeek.toString(),
        end = endOfWeek.toString(),
        label = "${startOfWeek.monthValue}.${startOfWeek.dayOfMonth}-${endOfWeek.monthValue}.${endOfWeek.dayOfMonth}"
    )
}

private fun computeCurrentMonthPeriod(): TimePeriod {
    val now = LocalDate.now()
    val yearMonth = YearMonth.from(now)
    return TimePeriod(
        type = "month",
        start = yearMonth.atDay(1).toString(),
        end = yearMonth.atEndOfMonth().toString(),
        label = "${yearMonth.year}年${yearMonth.monthValue}月"
    )
}

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
                recomputeStats()
            }
        }
    }

    fun setPeriodType(type: Int) {
        val now = LocalDate.now()
        val period = when (type) {
            0 -> {
                val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                TimePeriod("week", startOfWeek.toString(), endOfWeek.toString(),
                    "${startOfWeek.monthValue}.${startOfWeek.dayOfMonth}-${endOfWeek.monthValue}.${endOfWeek.dayOfMonth}")
            }
            1 -> {
                val yearMonth = YearMonth.from(now)
                TimePeriod("month", yearMonth.atDay(1).toString(), yearMonth.atEndOfMonth().toString(),
                    "${yearMonth.year}年${yearMonth.monthValue}月")
            }
            2 -> {
                TimePeriod("year", "${now.year}-01-01", "${now.year}-12-31",
                    "${now.year}年")
            }
            3 -> {
                _uiState.value.currentPeriod
            }
            else -> _uiState.value.currentPeriod
        }
        _uiState.update { it.copy(periodType = type, currentPeriod = period) }
        updateIsAtCurrentPeriod()
        recomputeStats()
    }

    fun navigatePeriod(direction: Int) {
        val current = _uiState.value.currentPeriod
        val currentStart = LocalDate.parse(current.start)
        val newPeriod = when (current.type) {
            "week" -> {
                val newStart = currentStart.plusWeeks(direction.toLong())
                val newEnd = newStart.plusDays(6)
                TimePeriod("week", newStart.toString(), newEnd.toString(),
                    "${newStart.monthValue}.${newStart.dayOfMonth}-${newEnd.monthValue}.${newEnd.dayOfMonth}")
            }
            "month" -> {
                val yearMonth = YearMonth.from(currentStart).plusMonths(direction.toLong())
                TimePeriod("month", yearMonth.atDay(1).toString(), yearMonth.atEndOfMonth().toString(),
                    "${yearMonth.year}年${yearMonth.monthValue}月")
            }
            "year" -> {
                val newYear = currentStart.year + direction
                TimePeriod("year", "$newYear-01-01", "$newYear-12-31",
                    "${newYear}年")
            }
            else -> current
        }
        _uiState.update { it.copy(currentPeriod = newPeriod) }
        updateIsAtCurrentPeriod()
        recomputeStats()
    }

    fun setBillType(type: Int) {
        _uiState.update { it.copy(billType = type) }
        recomputeStats()
    }

    fun setCustomPeriod(start: String, end: String) {
        val startDate = LocalDate.parse(start)
        val endDate = LocalDate.parse(end)
        val formatter = DateTimeFormatter.ofPattern("M.d")
        val period = TimePeriod(
            type = "custom",
            start = start,
            end = end,
            label = "${startDate.format(formatter)}-${endDate.format(formatter)}"
        )
        _uiState.update { it.copy(periodType = 3, currentPeriod = period) }
        updateIsAtCurrentPeriod()
        recomputeStats()
    }

    private fun updateIsAtCurrentPeriod() {
        val current = _uiState.value.currentPeriod
        val now = LocalDate.now()
        val isAtCurrent = when (current.type) {
            "week" -> {
                val currentWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                current.start == currentWeekStart.toString()
            }
            "month" -> {
                val currentMonth = YearMonth.from(now)
                current.start == currentMonth.atDay(1).toString()
            }
            "year" -> current.start == "${now.year}-01-01"
            else -> current.end >= now.toString()
        }
        _uiState.update { it.copy(isAtCurrentPeriod = isAtCurrent) }
    }

    private fun recomputeStats() {
        viewModelScope.launch {
            val period = _uiState.value.currentPeriod
            val bills = billRepository.getBillsByDateRangeSuspend(period.start, period.end)

            val previousPeriodEnd = LocalDate.parse(period.start).minusDays(1)
            val previousPeriodStart = when (period.type) {
                "week" -> previousPeriodEnd.minusDays(6)
                "month" -> YearMonth.from(previousPeriodEnd).atDay(1)
                "year" -> LocalDate.of(previousPeriodEnd.year - 1, 1, 1)
                else -> {
                    val dayCount = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.parse(period.start), LocalDate.parse(period.end)
                    ) + 1
                    previousPeriodEnd.minusDays(dayCount - 1)
                }
            }
            val previousBills = billRepository.getBillsByDateRangeSuspend(
                previousPeriodStart.toString(), previousPeriodEnd.toString()
            )

            val stats = computeStats(bills, previousBills, period, _uiState.value.categories)
            _uiState.update { it.copy(statsData = stats) }
        }
    }

    private fun computeStats(bills: List<BillEntity>, previousBills: List<BillEntity>, period: TimePeriod, categories: List<CategoryEntity>): StatsData {
        val billType = _uiState.value.billType
        val filteredBills = if (billType == 0) bills.filter { it.type == "expense" } else bills.filter { it.type == "income" }

        val totalExpense = AmountUtils.centsToYuan(bills.filter { it.type == "expense" }.sumOf { it.amount })
        val totalIncome = AmountUtils.centsToYuan(bills.filter { it.type == "income" }.sumOf { it.amount })

        val startDate = LocalDate.parse(period.start)
        val endDate = LocalDate.parse(period.end)
        val dayCount = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
        val dailyAverage = if (dayCount > 0) {
            if (billType == 0) totalExpense / dayCount else totalIncome / dayCount
        } else 0.0

        val balance = totalIncome - totalExpense

        val previousTotal = if (billType == 0) AmountUtils.centsToYuan(previousBills.filter { it.type == "expense" }.sumOf { it.amount }) else AmountUtils.centsToYuan(previousBills.filter { it.type == "income" }.sumOf { it.amount })
        val currentTotal = if (billType == 0) totalExpense else totalIncome
        val comparedToLast = if (previousTotal > 0) {
            (currentTotal - previousTotal) / previousTotal * 100
        } else 0.0

        val trendData = computeTrendData(bills, period)

        val categoryData = computeCategoryData(filteredBills, categories)

        return StatsData(
            totalExpense = totalExpense,
            totalIncome = totalIncome,
            dailyAverage = dailyAverage,
            comparedToLast = comparedToLast,
            balance = balance,
            trendData = trendData,
            categoryData = categoryData
        )
    }

    private fun computeTrendData(bills: List<BillEntity>, period: TimePeriod): List<TrendPoint> {
        val startDate = LocalDate.parse(period.start)
        val endDate = LocalDate.parse(period.end)
        val grouped = bills.groupBy { it.date }

        return when (period.type) {
            "week" -> {
                val startOfWeek = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                (0..6).map { offset ->
                    val date = startOfWeek.plusDays(offset.toLong())
                    val dayBills = grouped[date.toString()] ?: emptyList()
                    TrendPoint(
                        date = "${date.monthValue}/${date.dayOfMonth}",
                        expense = AmountUtils.centsToYuan(dayBills.filter { it.type == "expense" }.sumOf { it.amount }),
                        income = AmountUtils.centsToYuan(dayBills.filter { it.type == "income" }.sumOf { it.amount })
                    )
                }
            }
            "month" -> {
                val year = startDate.year
                (1..12).map { month ->
                    val monthStart = LocalDate.of(year, month, 1)
                    val monthEnd = YearMonth.of(year, month).atEndOfMonth()
                    val monthBills = bills.filter { bill ->
                        bill.date >= monthStart.toString() && bill.date <= monthEnd.toString()
                    }
                    TrendPoint(
                        date = "${month}月",
                        expense = AmountUtils.centsToYuan(monthBills.filter { it.type == "expense" }.sumOf { it.amount }),
                        income = AmountUtils.centsToYuan(monthBills.filter { it.type == "income" }.sumOf { it.amount })
                    )
                }
            }
            "year" -> {
                val currentYear = LocalDate.now().year
                (currentYear - 4..currentYear).map { year ->
                    val yearStart = LocalDate.of(year, 1, 1)
                    val yearEnd = LocalDate.of(year, 12, 31)
                    val yearBills = bills.filter { bill ->
                        bill.date >= yearStart.toString() && bill.date <= yearEnd.toString()
                    }
                    TrendPoint(
                        date = "${year}",
                        expense = AmountUtils.centsToYuan(yearBills.filter { it.type == "expense" }.sumOf { it.amount }),
                        income = AmountUtils.centsToYuan(yearBills.filter { it.type == "income" }.sumOf { it.amount })
                    )
                }
            }
            else -> {
                val dayCount = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
                (0 until dayCount).map { offset ->
                    val date = startDate.plusDays(offset)
                    val dayBills = grouped[date.toString()] ?: emptyList()
                    TrendPoint(
                        date = "${date.monthValue}/${date.dayOfMonth}",
                        expense = AmountUtils.centsToYuan(dayBills.filter { it.type == "expense" }.sumOf { it.amount }),
                        income = AmountUtils.centsToYuan(dayBills.filter { it.type == "income" }.sumOf { it.amount })
                    )
                }
            }
        }
    }

    private fun computeCategoryData(bills: List<BillEntity>, categories: List<CategoryEntity>): List<CategoryStat> {
        val totalAmount = AmountUtils.centsToYuan(bills.sumOf { it.amount })
        val categoryMap = categories.associateBy { it.id }

        return bills.groupBy { it.category }
            .map { (categoryId, categoryBills) ->
                val cat = categoryMap[categoryId]
                val amount = AmountUtils.centsToYuan(categoryBills.sumOf { it.amount })
                CategoryStat(
                    categoryId = categoryId,
                    categoryName = cat?.name ?: categoryId,
                    categoryIcon = cat?.icon ?: "",
                    amount = amount,
                    count = categoryBills.size,
                    percentage = if (totalAmount > 0) (amount / totalAmount * 100).toFloat() else 0f
                )
            }
            .sortedByDescending { it.amount }
    }

    fun formatAmount(amount: Double): String {
        val df = DecimalFormat("#,##0.00")
        return df.format(amount)
    }
}

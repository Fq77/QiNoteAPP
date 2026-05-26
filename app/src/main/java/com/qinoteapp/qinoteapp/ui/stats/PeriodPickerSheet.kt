package com.qinoteapp.qinoteapp.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.components.SheetDragHandle
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodPickerSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    currentStart: String,
    currentEnd: String,
    periodType: Int = 0
) {
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedWeek by remember { mutableStateOf(LocalDate.now()) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedYear by remember { mutableStateOf(Year.now()) }
    var customStart by remember { mutableStateOf(LocalDate.now().minusDays(7)) }
    var customEnd by remember { mutableStateOf(LocalDate.now()) }
    var weekViewMonth by remember { mutableStateOf(YearMonth.now()) }

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = colors.Surface,
            dragHandle = { SheetDragHandle() },
            shape = RoundedCornerShape(topStart = QiRadius.xxl, topEnd = QiRadius.xxl)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl)
                    .padding(bottom = Spacing.xxxl)
            ) {
                Text(
                    text = "选择时间",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = colors.OnBackground
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                when (periodType) {
                    0 -> WeekPickerGrid(
                        selectedWeek = selectedWeek,
                        viewMonth = weekViewMonth,
                        onWeekSelected = { selectedWeek = it },
                        onViewMonthChange = { weekViewMonth = it }
                    )
                    1 -> MonthPickerGrid(
                        selectedMonth = selectedMonth,
                        onMonthSelected = { selectedMonth = it }
                    )
                    2 -> YearPickerGrid(
                        selectedYear = selectedYear,
                        onYearSelected = { selectedYear = it }
                    )
                    3 -> CustomDateRangePicker(
                        start = customStart,
                        end = customEnd,
                        onStartChange = { customStart = it },
                        onEndChange = { customEnd = it }
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(colors.Primary, colors.PrimaryLight)
                            ),
                            RoundedCornerShape(QiRadius.lg)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                val (start, end) = when (periodType) {
                                    0 -> {
                                        val startOfWeek = selectedWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                        val endOfWeek = selectedWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                                        startOfWeek.toString() to endOfWeek.toString()
                                    }
                                    1 -> {
                                        val startOfMonth = selectedMonth.atDay(1)
                                        val endOfMonth = selectedMonth.atEndOfMonth()
                                        startOfMonth.toString() to endOfMonth.toString()
                                    }
                                    2 -> {
                                        val startOfYear = selectedYear.atDay(1)
                                        val endOfYear = selectedYear.atDay(selectedYear.length())
                                        startOfYear.toString() to endOfYear.toString()
                                    }
                                    else -> customStart.toString() to customEnd.toString()
                                }
                                onConfirm(start, end)
                            }
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "确定",
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 15.sp,
                        color = colors.OnPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekPickerGrid(
    selectedWeek: LocalDate,
    viewMonth: YearMonth,
    onWeekSelected: (LocalDate) -> Unit,
    onViewMonthChange: (YearMonth) -> Unit
) {
    val colors = QiTheme.colors
    val weeks = remember(viewMonth) { computeWeeksForMonth(viewMonth) }
    val currentWeekStart = remember {
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
    val selectedWeekStart = selectedWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onViewMonthChange(viewMonth.minusMonths(1)) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "上一月",
                    tint = colors.OnSurface
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = "${viewMonth.year}年${viewMonth.monthValue}月",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = colors.OnBackground
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            IconButton(onClick = { onViewMonthChange(viewMonth.plusMonths(1)) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "下一月",
                    tint = colors.OnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            items(weeks) { weekRange ->
                val isSelected = weekRange.start == selectedWeekStart
                val isCurrent = weekRange.start == currentWeekStart
                PickerCell(
                    text = weekRange.label,
                    isSelected = isSelected,
                    isCurrent = isCurrent && !isSelected,
                    onClick = { onWeekSelected(weekRange.start) }
                )
            }
        }
    }
}

@Composable
private fun MonthPickerGrid(
    selectedMonth: YearMonth,
    onMonthSelected: (YearMonth) -> Unit
) {
    val colors = QiTheme.colors
    var viewYear by remember { mutableStateOf(selectedMonth.year) }
    val currentMonth = YearMonth.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewYear-- }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "上一年",
                    tint = colors.OnSurface
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = "${viewYear}年",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = colors.OnBackground
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            IconButton(onClick = { viewYear++ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "下一年",
                    tint = colors.OnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            items((1..12).toList()) { month ->
                val monthValue = YearMonth.of(viewYear, month)
                val isSelected = monthValue == selectedMonth
                val isCurrent = monthValue == currentMonth
                PickerCell(
                    text = "${month}月",
                    isSelected = isSelected,
                    isCurrent = isCurrent && !isSelected,
                    onClick = { onMonthSelected(monthValue) }
                )
            }
        }
    }
}

@Composable
private fun YearPickerGrid(
    selectedYear: Year,
    onYearSelected: (Year) -> Unit
) {
    val colors = QiTheme.colors
    val currentYear = Year.now()
    var decadeStart by remember { mutableStateOf((selectedYear.value / 10) * 10) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { decadeStart -= 10 }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "上一个十年",
                    tint = colors.OnSurface
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = "${decadeStart}-${decadeStart + 9}",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = colors.OnBackground
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            IconButton(onClick = { decadeStart += 10 }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "下一个十年",
                    tint = colors.OnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            items((0..9).toList()) { offset ->
                val yearValue = Year.of(decadeStart + offset)
                val isSelected = yearValue == selectedYear
                val isCurrent = yearValue == currentYear
                PickerCell(
                    text = "${yearValue.value}",
                    isSelected = isSelected,
                    isCurrent = isCurrent && !isSelected,
                    onClick = { onYearSelected(yearValue) }
                )
            }
        }
    }
}

@Composable
private fun CustomDateRangePicker(
    start: LocalDate,
    end: LocalDate,
    onStartChange: (LocalDate) -> Unit,
    onEndChange: (LocalDate) -> Unit
) {
    val colors = QiTheme.colors
    var pickingStart by remember { mutableStateOf(true) }
    var calendarMonth by remember { mutableStateOf(YearMonth.from(start)) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            DateSelectItem(
                label = "开始",
                date = start,
                isActive = pickingStart,
                onClick = { pickingStart = true }
            )
            DateSelectItem(
                label = "结束",
                date = end,
                isActive = !pickingStart,
                onClick = { pickingStart = false }
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { calendarMonth = calendarMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "上一月",
                    tint = colors.OnSurface
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = "${calendarMonth.year}年${calendarMonth.monthValue}月",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = colors.OnBackground
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            IconButton(onClick = { calendarMonth = calendarMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "下一月",
                    tint = colors.OnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("一", "二", "三", "四", "五", "六", "日").forEach { dayName ->
                Text(
                    text = dayName,
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                    color = colors.TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        CalendarGrid(
            month = calendarMonth,
            selectedStart = start,
            selectedEnd = end,
            pickingStart = pickingStart,
            onDateSelected = { date ->
                if (pickingStart) {
                    onStartChange(date)
                    if (date.isAfter(end)) {
                        onEndChange(date.plusDays(1))
                    }
                    pickingStart = false
                } else {
                    if (date.isBefore(start)) {
                        onStartChange(date)
                        pickingStart = false
                    } else {
                        onEndChange(date)
                    }
                }
            }
        )
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    selectedStart: LocalDate,
    selectedEnd: LocalDate,
    pickingStart: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val colors = QiTheme.colors
    val firstDayOfMonth = month.atDay(1)
    val dayOfWeekOffset = (firstDayOfMonth.dayOfWeek.value - 1)
    val daysInMonth = month.lengthOfMonth()
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        val rows = ((dayOfWeekOffset + daysInMonth + 6) / 7)
        (0 until rows).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                (0..6).forEach { col ->
                    val dayIndex = row * 7 + col - dayOfWeekOffset + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = month.atDay(dayIndex)
                        val isSelected = date == selectedStart || date == selectedEnd
                        val isInRange = !date.isBefore(selectedStart) && !date.isAfter(selectedEnd) && selectedStart != selectedEnd
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .size(36.dp)
                                .clip(RoundedCornerShape(QiRadius.md))
                                .background(
                                    when {
                                        isSelected -> colors.Primary
                                        isInRange -> colors.PrimaryLighter
                                        isToday -> colors.Surface1
                                        else -> colors.Surface.copy(alpha = 0f)
                                    }
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onDateSelected(date) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${date.dayOfMonth}",
                                fontFamily = JakartaFontFamily,
                                fontWeight = if (isSelected) FontWeight.W600 else FontWeight.W400,
                                fontSize = 13.sp,
                                color = when {
                                    isSelected -> colors.OnPrimary
                                    isToday -> colors.Primary
                                    else -> colors.OnBackground
                                }
                            )
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).size(36.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DateSelectItem(
    label: String,
    date: LocalDate,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val colors = QiTheme.colors
    val formatter = DateTimeFormatter.ofPattern("M月d日")
    Row(
        modifier = Modifier
            .weight(1f)
            .background(
                if (isActive) colors.PrimaryLighter else colors.Surface1,
                RoundedCornerShape(QiRadius.md)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            color = if (isActive) colors.Primary else colors.TextTertiary
        )
        Text(
            text = date.format(formatter),
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            color = if (isActive) colors.Primary else colors.OnBackground
        )
    }
}

@Composable
private fun PickerCell(
    text: String,
    isSelected: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val colors = QiTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(QiRadius.md))
            .background(
                when {
                    isSelected -> colors.Primary
                    isCurrent -> colors.PrimaryLighter
                    else -> colors.Surface1
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = JakartaFontFamily,
            fontWeight = if (isSelected) FontWeight.W600 else FontWeight.W400,
            fontSize = 13.sp,
            color = when {
                isSelected -> colors.OnPrimary
                isCurrent -> colors.Primary
                else -> colors.OnBackground
            }
        )
    }
}

private data class WeekRange(
    val start: LocalDate,
    val end: LocalDate,
    val label: String
)

private fun computeWeeksForMonth(yearMonth: YearMonth): List<WeekRange> {
    val formatter = DateTimeFormatter.ofPattern("M.d")
    val weeks = mutableListOf<WeekRange>()
    var current = yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val monthEnd = yearMonth.atEndOfMonth()

    while (!current.isAfter(monthEnd)) {
        val weekEnd = current.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        weeks.add(
            WeekRange(
                start = current,
                end = weekEnd,
                label = "${current.format(formatter)}-${weekEnd.format(formatter)}"
            )
        )
        current = weekEnd.plusDays(1)
    }

    return weeks
}

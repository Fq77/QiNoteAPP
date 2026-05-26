package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSheet(
    visible: Boolean,
    currentDate: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedDate by remember {
        mutableStateOf(try { LocalDate.parse(currentDate) } catch (_: Exception) { LocalDate.now() })
    }
    var viewMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

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
                    text = "选择日期",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = colors.OnBackground
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewMonth = viewMonth.minusMonths(1) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
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
                    IconButton(onClick = { viewMonth = viewMonth.plusMonths(1) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
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

                DatePickerCalendarGrid(
                    month = viewMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date
                        viewMonth = YearMonth.from(date)
                    }
                )

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
                            onClick = { onConfirm(selectedDate.toString()) }
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
private fun DatePickerCalendarGrid(
    month: YearMonth,
    selectedDate: LocalDate,
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
                        val isSelected = date == selectedDate
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .size(36.dp)
                                .clip(RoundedCornerShape(QiRadius.md))
                                .background(
                                    when {
                                        isSelected -> colors.Primary
                                        isToday -> colors.PrimaryLighter
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSheet(
    visible: Boolean,
    currentTime: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = QiTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val parsedTime = remember(currentTime) {
        try {
            LocalTime.parse(currentTime, DateTimeFormatter.ofPattern("HH:mm:ss"))
        } catch (_: Exception) {
            try {
                LocalTime.parse(currentTime, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (_: Exception) {
                LocalTime.now()
            }
        }
    }
    var selectedHour by remember { mutableStateOf(parsedTime.hour) }
    var selectedMinute by remember { mutableStateOf((parsedTime.minute / 5) * 5) }
    var selectedSecond by remember { mutableStateOf((parsedTime.second / 5) * 5) }

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

                Spacer(modifier = Modifier.height(Spacing.lg))

                Text(
                    text = "小时",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp,
                    color = colors.TextTertiary
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    items((0..23).toList()) { hour ->
                        TimePickerCell(
                            text = String.format("%02d", hour),
                            isSelected = hour == selectedHour,
                            onClick = { selectedHour = hour }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Text(
                    text = "分钟",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp,
                    color = colors.TextTertiary
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    items((0..55 step 5).toList()) { minute ->
                        TimePickerCell(
                            text = String.format("%02d", minute),
                            isSelected = minute == selectedMinute,
                            onClick = { selectedMinute = minute }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Text(
                    text = "秒",
                    fontFamily = JakartaFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp,
                    color = colors.TextTertiary
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    items((0..55 step 5).toList()) { second ->
                        TimePickerCell(
                            text = String.format("%02d", second),
                            isSelected = second == selectedSecond,
                            onClick = { selectedSecond = second }
                        )
                    }
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
                                val time = LocalTime.of(selectedHour, selectedMinute, selectedSecond)
                                onConfirm(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
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
private fun TimePickerCell(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = QiTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(QiRadius.md))
            .background(if (isSelected) colors.Primary else colors.Surface1)
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
            color = if (isSelected) colors.OnPrimary else colors.OnBackground
        )
    }
}

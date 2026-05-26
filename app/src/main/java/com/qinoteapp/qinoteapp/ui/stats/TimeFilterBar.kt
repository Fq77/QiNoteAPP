package com.qinoteapp.qinoteapp.ui.stats

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import com.qinoteapp.qinoteapp.components.SegmentedControl

@Composable
fun TimeFilterBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    SegmentedControl(
        segments = listOf("周", "月", "年", "自定义"),
        selectedIndex = selectedIndex,
        onSegmentSelected = onSelected,
        icons = listOf(
            Icons.Filled.DateRange,
            Icons.Filled.CalendarMonth,
            Icons.Filled.CalendarToday,
            Icons.Filled.Tune
        )
    )
}

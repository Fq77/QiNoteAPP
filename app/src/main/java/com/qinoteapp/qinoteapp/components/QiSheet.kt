package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape: Shape = RoundedCornerShape(topStart = QiRadius.xxl, topEnd = QiRadius.xxl),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = QiTheme.colors.Surface,
        dragHandle = { SheetDragHandle() },
        shape = shape,
        content = content
    )
}

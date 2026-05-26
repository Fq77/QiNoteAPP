package com.qinoteapp.qinoteapp.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp

object QiElevation {
    val none = 0.dp
    val xs = 1.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 8.dp
    val xl = 12.dp
    val xxl = 16.dp
}

object QiShadow {
    val sm = Shadow(color = Color.Black, offset = Offset(0f, 1f), blurRadius = 2f)
    val md = Shadow(color = Color.Black, offset = Offset(0f, 2f), blurRadius = 4f)
    val lg = Shadow(color = Color.Black, offset = Offset(0f, 4f), blurRadius = 8f)
}

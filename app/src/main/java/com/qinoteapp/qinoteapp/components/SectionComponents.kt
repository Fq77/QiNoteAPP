package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeader(
    title: String,
    subtitle: String
) {
    val colors = QiTheme.colors
    Column(modifier = Modifier.padding(bottom = Spacing.sm)) {
        Text(
            text = title,
            fontFamily = OutfitFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 15.sp,
            color = colors.OnBackground
        )
        Text(
            text = subtitle,
            fontFamily = JakartaFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            color = colors.TextTertiary
        )
    }
}

@Composable
fun SectionCard(
    content: @Composable () -> Unit
) {
    val colors = QiTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(QiRadius.xl),
        color = colors.Surface
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            content()
        }
    }
}

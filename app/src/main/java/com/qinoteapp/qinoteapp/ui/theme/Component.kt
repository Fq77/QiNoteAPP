package com.qinoteapp.qinoteapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class QiTextFieldColors(
    val focusedBorderColor: Color,
    val unfocusedBorderColor: Color,
    val cursorColor: Color,
    val focusedContainerColor: Color,
    val unfocusedContainerColor: Color,
    val focusedTextColor: Color,
    val unfocusedTextColor: Color,
    val disabledBorderColor: Color,
    val disabledContainerColor: Color,
    val disabledTextColor: Color,
    val disabledTrailingIconColor: Color,
    val disabledLabelColor: Color,
    val focusedLabelColor: Color,
    val unfocusedLabelColor: Color
)

fun qiLightTextFieldColors(): QiTextFieldColors = QiTextFieldColors(
    focusedBorderColor = QiLightColors.Primary,
    unfocusedBorderColor = QiLightColors.BorderStrong,
    cursorColor = QiLightColors.Primary,
    focusedContainerColor = QiLightColors.Surface,
    unfocusedContainerColor = QiLightColors.Surface,
    focusedTextColor = QiLightColors.OnBackground,
    unfocusedTextColor = QiLightColors.OnBackground,
    disabledBorderColor = QiLightColors.Border,
    disabledContainerColor = QiLightColors.Surface1,
    disabledTextColor = QiLightColors.TextTertiary,
    disabledTrailingIconColor = QiLightColors.TextTertiary,
    disabledLabelColor = QiLightColors.TextTertiary,
    focusedLabelColor = QiLightColors.Primary,
    unfocusedLabelColor = QiLightColors.OnSurface
)

fun qiDarkTextFieldColors(): QiTextFieldColors = QiTextFieldColors(
    focusedBorderColor = QiDarkColors.Primary,
    unfocusedBorderColor = QiDarkColors.BorderStrong,
    cursorColor = QiDarkColors.Primary,
    focusedContainerColor = QiDarkColors.Surface,
    unfocusedContainerColor = QiDarkColors.Surface,
    focusedTextColor = QiDarkColors.OnBackground,
    unfocusedTextColor = QiDarkColors.OnBackground,
    disabledBorderColor = QiDarkColors.Border,
    disabledContainerColor = QiDarkColors.Surface1,
    disabledTextColor = QiDarkColors.TextTertiary,
    disabledTrailingIconColor = QiDarkColors.TextTertiary,
    disabledLabelColor = QiDarkColors.TextTertiary,
    focusedLabelColor = QiDarkColors.Primary,
    unfocusedLabelColor = QiDarkColors.OnSurface
)

data class QiButtonStyle(
    val primaryGradientColors: List<Color>,
    val secondaryBgColor: Color,
    val shape: RoundedCornerShape,
    val textStyle: TextStyle
)

fun qiLightButtonStyle(): QiButtonStyle = QiButtonStyle(
    primaryGradientColors = listOf(QiLightColors.Primary, QiLightColors.PrimaryLight),
    secondaryBgColor = QiLightColors.Surface1,
    shape = RoundedCornerShape(12.dp),
    textStyle = TextStyle(
        fontFamily = JakartaFontFamily,
        fontWeight = androidx.compose.ui.text.font.FontWeight.W600,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

fun qiDarkButtonStyle(): QiButtonStyle = QiButtonStyle(
    primaryGradientColors = listOf(QiDarkColors.Primary, QiDarkColors.PrimaryLight),
    secondaryBgColor = QiDarkColors.Surface1,
    shape = RoundedCornerShape(12.dp),
    textStyle = TextStyle(
        fontFamily = JakartaFontFamily,
        fontWeight = androidx.compose.ui.text.font.FontWeight.W600,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)

val LocalQiTextFieldColors = compositionLocalOf { qiLightTextFieldColors() }
val LocalQiButtonStyle = compositionLocalOf { qiLightButtonStyle() }



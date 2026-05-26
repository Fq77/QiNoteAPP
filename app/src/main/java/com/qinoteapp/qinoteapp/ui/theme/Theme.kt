package com.qinoteapp.qinoteapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class ThemeMode { LIGHT, DARK, SYSTEM }

class QiThemeState(initialMode: ThemeMode) {
    var themeMode by mutableStateOf(initialMode)
}

val LocalQiThemeState = compositionLocalOf { QiThemeState(ThemeMode.SYSTEM) }
val LocalQiColors = compositionLocalOf { QiLightColors }

object QiTheme {
    val colors: QiColorScheme
        @Composable get() = LocalQiColors.current

    val textFieldColors: QiTextFieldColors
        @Composable get() = LocalQiTextFieldColors.current

    val buttonStyle: QiButtonStyle
        @Composable get() = LocalQiButtonStyle.current
}

private val QiLightColorScheme = lightColorScheme(
    primary = QiLightColors.Primary,
    onPrimary = QiLightColors.OnPrimary,
    primaryContainer = QiLightColors.PrimaryLight,
    secondary = QiLightColors.OnSurface,
    onSecondary = QiLightColors.OnPrimary,
    tertiary = QiLightColors.Income,
    background = QiLightColors.Background,
    onBackground = QiLightColors.OnBackground,
    surface = QiLightColors.Surface,
    onSurface = QiLightColors.OnBackground,
    surfaceVariant = QiLightColors.PrimaryLighter,
    onSurfaceVariant = QiLightColors.OnSurface,
    error = QiLightColors.Expense,
    onError = QiLightColors.OnPrimary,
    outline = QiLightColors.BorderStrong,
    outlineVariant = QiLightColors.Border,
)

private val QiDarkColorScheme = darkColorScheme(
    primary = QiDarkColors.Primary,
    onPrimary = QiDarkColors.OnPrimary,
    primaryContainer = QiDarkColors.PrimaryLight,
    secondary = QiDarkColors.OnSurface,
    onSecondary = QiDarkColors.OnPrimary,
    tertiary = QiDarkColors.Income,
    background = QiDarkColors.Background,
    onBackground = QiDarkColors.OnBackground,
    surface = QiDarkColors.Surface,
    onSurface = QiDarkColors.OnBackground,
    surfaceVariant = QiDarkColors.PrimaryLighter,
    onSurfaceVariant = QiDarkColors.OnSurface,
    error = QiDarkColors.Expense,
    onError = QiDarkColors.OnPrimary,
    outline = QiDarkColors.BorderStrong,
    outlineVariant = QiDarkColors.Border,
)

@Composable
fun QiNoteAPPTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (useDarkTheme) QiDarkColorScheme else QiLightColorScheme
    val qiColors = if (useDarkTheme) QiDarkColors else QiLightColors
    val themeState = remember { QiThemeState(themeMode) }
    themeState.themeMode = themeMode

    val textFieldColors = if (useDarkTheme) qiDarkTextFieldColors() else qiLightTextFieldColors()
    val buttonStyle = if (useDarkTheme) qiDarkButtonStyle() else qiLightButtonStyle()

    CompositionLocalProvider(
        LocalQiColors provides qiColors,
        LocalQiThemeState provides themeState,
        LocalQiTextFieldColors provides textFieldColors,
        LocalQiButtonStyle provides buttonStyle
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = QiTypography,
            content = content
        )
    }
}

package com.qinoteapp.qinoteapp.ui.theme

import androidx.compose.ui.graphics.Color

data class QiColorScheme(
    val Primary: Color,
    val PrimaryLight: Color,
    val PrimaryLighter: Color,
    val PrimaryDark: Color,
    val PrimaryGlow: Color,
    val Accent: Color,
    val AccentSoft: Color,
    val Expense: Color,
    val ExpenseSoft: Color,
    val Income: Color,
    val IncomeSoft: Color,
    val Background: Color,
    val Surface: Color,
    val SurfaceElevated: Color,
    val Surface1: Color,
    val Surface2: Color,
    val OnBackground: Color,
    val OnSurface: Color,
    val TextTertiary: Color,
    val TextQuaternary: Color,
    val OnPrimary: Color,
    val OnPrimarySoft: Color,
    val OnPrimaryMuted: Color,
    val Border: Color,
    val BorderStrong: Color
)

val QiLightColors = QiColorScheme(
    Primary = Color(0xFFFF6900),
    PrimaryLight = Color(0xFFFF8A33),
    PrimaryLighter = Color(0xFFFFF0E5),
    PrimaryDark = Color(0xFFE05500),
    PrimaryGlow = Color(0x33FF6900),
    Accent = Color(0xFF2A9D8F),
    AccentSoft = Color(0xFFE8F5F3),
    Expense = Color(0xFFDC3545),
    ExpenseSoft = Color(0xFFFEF2F2),
    Income = Color(0xFF0D9668),
    IncomeSoft = Color(0xFFECFDF5),
    Background = Color(0xFFFAFAF7),
    Surface = Color(0xFFFFFFFF),
    SurfaceElevated = Color(0xFFFFFFFF),
    Surface1 = Color(0xFFF3F2EF),
    Surface2 = Color(0xFFE8E6E2),
    OnBackground = Color(0xFF1A1917),
    OnSurface = Color(0xFF57534E),
    TextTertiary = Color(0xFFA8A29E),
    TextQuaternary = Color(0xFFD6D3D1),
    OnPrimary = Color(0xFFFFFFFF),
    OnPrimarySoft = Color(0xFFFFB3B3),
    OnPrimaryMuted = Color(0xFFFFD4D4),
    Border = Color(0x0A1A1917),
    BorderStrong = Color(0x1A1A1917)
)

val QiDarkColors = QiColorScheme(
    Primary = Color(0xFFFF8A33),
    PrimaryLight = Color(0xFFFFB366),
    PrimaryLighter = Color(0xFF3D2216),
    PrimaryDark = Color(0xFFE05500),
    PrimaryGlow = Color(0x4DFF6900),
    Accent = Color(0xFF40C9B0),
    AccentSoft = Color(0xFF1A3330),
    Expense = Color(0xFFF87171),
    ExpenseSoft = Color(0xFF3B1515),
    Income = Color(0xFF34D399),
    IncomeSoft = Color(0xFF0A2E1F),
    Background = Color(0xFF0D0D0D),
    Surface = Color(0xFF1E1E24),
    SurfaceElevated = Color(0xFF242220),
    Surface1 = Color(0xFF28282E),
    Surface2 = Color(0xFF34343A),
    OnBackground = Color(0xFFF5F5F0),
    OnSurface = Color(0xFFD6D3D1),
    TextTertiary = Color(0xFF78716C),
    TextQuaternary = Color(0xFF57534E),
    OnPrimary = Color(0xFF1A1917),
    OnPrimarySoft = Color(0xFF8B3A3A),
    OnPrimaryMuted = Color(0xFF5C2D2D),
    Border = Color(0x15F5F5F0),
    BorderStrong = Color(0x25F5F5F0)
)

object CategoryColors {
    val Food = Color(0xFFD97706)
    val Transport = Color(0xFF6366F1)
    val Shopping = Color(0xFFEC4899)
    val Entertainment = Color(0xFF16A34A)
    val Living = Color(0xFF8B5CF6)
    val Daily = Color(0xFFEA580C)
    val Health = Color(0xFFEF4444)
    val Education = Color(0xFF22C55E)
    val Social = Color(0xFFF97316)
    val Pet = Color(0xFF7C3AED)
    val InvestExpense = Color(0xFF059669)
    val Digital = Color(0xFF0284C7)
    val Housing = Color(0xFFCA8A04)
    val Salary = Color(0xFF10B981)
    val Freelance = Color(0xFFF97316)
    val Investment = Color(0xFF0284C7)
    val AllColors = listOf(
        Color(0xFFD97706), Color(0xFF6366F1), Color(0xFFEC4899),
        Color(0xFF16A34A), Color(0xFF8B5CF6), Color(0xFFEA580C),
        Color(0xFFEF4444), Color(0xFF22C55E), Color(0xFFF97316),
        Color(0xFF7C3AED), Color(0xFF059669), Color(0xFF0284C7),
        Color(0xFFCA8A04), Color(0xFF0EA5E9), Color(0xFFE11D48),
        Color(0xFF14B8A6)
    )
}

object CategoryColorsDark {
    val Food = Color(0xFFF59E0B)
    val Transport = Color(0xFF818CF8)
    val Shopping = Color(0xFFF472B6)
    val Entertainment = Color(0xFF4ADE80)
    val Living = Color(0xFFA78BFA)
    val Daily = Color(0xFFFB923C)
    val Health = Color(0xFFF87171)
    val Education = Color(0xFF4ADE80)
    val Social = Color(0xFFFB923C)
    val Pet = Color(0xFFA78BFA)
    val InvestExpense = Color(0xFF34D399)
    val Digital = Color(0xFF38BDF8)
    val Housing = Color(0xFFFBBF24)
    val Salary = Color(0xFF34D399)
    val Freelance = Color(0xFFFB923C)
    val Investment = Color(0xFF38BDF8)
    val AllColors = listOf(
        Color(0xFFF59E0B), Color(0xFF818CF8), Color(0xFFF472B6),
        Color(0xFF4ADE80), Color(0xFFA78BFA), Color(0xFFFB923C),
        Color(0xFFF87171), Color(0xFF4ADE80), Color(0xFFFB923C),
        Color(0xFFA78BFA), Color(0xFF34D399), Color(0xFF38BDF8),
        Color(0xFFFBBF24), Color(0xFF38BDF8), Color(0xFFFB7185),
        Color(0xFF2DD4BF)
    )
}

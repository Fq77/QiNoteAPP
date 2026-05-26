package com.qinoteapp.qinoteapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.R

val OutfitFontFamily = FontFamily(Font(R.font.outfit))
val JakartaFontFamily = FontFamily(Font(R.font.plus_jakarta_sans))

val QiTypography = Typography(
    displayLarge = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W800, fontSize = 40.sp, lineHeight = 48.sp),
    displayMedium = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W700, fontSize = 28.sp, lineHeight = 34.sp),
    headlineLarge = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W700, fontSize = 24.sp, lineHeight = 30.sp),
    headlineMedium = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W600, fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W600, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W400, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W400, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W400, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 13.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W500, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W500, fontSize = 11.sp, lineHeight = 14.sp)
)

val QiCardLabel = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, lineHeight = 20.sp)
val QiCardLabelSmall = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W500, fontSize = 11.sp, lineHeight = 14.sp)
val QiAmountLarge = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W800, fontSize = 44.sp, lineHeight = 52.sp)
val QiAmountSign = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W700, fontSize = 28.sp, lineHeight = 44.sp)
val QiAmountMedium = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W700, fontSize = 14.sp, lineHeight = 20.sp)
val QiAmountSmall = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W600, fontSize = 12.sp, lineHeight = 16.sp)
val QiSectionTitle = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, lineHeight = 20.sp)
val QiPeriodLabel = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W600, fontSize = 15.sp, lineHeight = 20.sp)
val QiSettingsCardTitle = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W600, fontSize = 15.sp, lineHeight = 20.sp)
val QiDetailTitle = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W700, fontSize = 18.sp, lineHeight = 24.sp)
val QiDetailAmount = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W800, fontSize = 24.sp, lineHeight = 30.sp)
val QiDetailLabel = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W400, fontSize = 13.sp, lineHeight = 18.sp)
val QiDetailValue = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W500, fontSize = 14.sp, lineHeight = 20.sp)
val QiFieldLabel = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, lineHeight = 20.sp)
val QiToastMessage = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = (-0.2).sp)
val QiToastAction = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 13.sp, lineHeight = 18.sp)
val QiButtonPrimary = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 15.sp, lineHeight = 20.sp)
val QiButtonSecondary = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W500, fontSize = 14.sp, lineHeight = 20.sp)
val QiDonutCenterAmount = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.W700, fontSize = 18.sp, lineHeight = 24.sp)
val QiDonutCenterLabel = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W400, fontSize = 11.sp, lineHeight = 14.sp)
val QiNavLabel = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W400, fontSize = 11.sp, lineHeight = 14.sp)
val QiNavLabelSelected = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W600, fontSize = 11.sp, lineHeight = 14.sp)
val QiUploadLabel = TextStyle(fontFamily = JakartaFontFamily, fontWeight = FontWeight.W500, fontSize = 13.sp, lineHeight = 18.sp)

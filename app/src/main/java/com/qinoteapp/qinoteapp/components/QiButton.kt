package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.ui.theme.QiButtonPrimary
import com.qinoteapp.qinoteapp.ui.theme.QiButtonSecondary
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

object QiButton {

    @Composable
    fun Primary(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true
    ) {
        val colors = QiTheme.colors
        val gradientColors = if (enabled) listOf(colors.Primary, colors.PrimaryLight) else listOf(colors.Primary.copy(alpha = 0.4f), colors.PrimaryLight.copy(alpha = 0.4f))
        val contentColor = if (enabled) colors.OnPrimary else colors.OnPrimary.copy(alpha = 0.5f)

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(QiRadius.lg))
                .qiPressScale()
                .background(
                    Brush.horizontalGradient(colors = gradientColors),
                    RoundedCornerShape(QiRadius.lg)
                )
                .then(
                    if (enabled) Modifier.clickable { onClick() }
                    else Modifier
                )
                .padding(vertical = Spacing.sm),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = QiButtonPrimary,
                color = contentColor
            )
        }
    }

    @Composable
    fun Secondary(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: ImageVector? = null,
        iconTint: Color? = null,
        enabled: Boolean = true
    ) {
        val colors = QiTheme.colors
        val tintColor = iconTint ?: colors.Primary
        val contentColor = if (enabled) colors.Primary else colors.Primary.copy(alpha = 0.4f)
        val bgColor = if (enabled) colors.Primary.copy(alpha = 0.1f) else colors.Primary.copy(alpha = 0.04f)

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(QiRadius.md))
                .qiPressScale()
                .background(bgColor)
                .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) tintColor else tintColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                }
                Text(
                    text = text,
                    style = QiButtonSecondary,
                    color = colors.Primary
                )
            }
        }
    }
}

package com.qinoteapp.qinoteapp.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.OutfitFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun RowScope.QiStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = QiTheme.colors.OnBackground,
    weight: Float = 1f,
    pressEnabled: Boolean = true
) {
    QiStatCardInternal(
        title = title,
        value = value,
        modifier = modifier.weight(weight),
        valueColor = valueColor,
        pressEnabled = pressEnabled
    )
}

@Composable
fun QiStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = QiTheme.colors.OnBackground,
    pressEnabled: Boolean = true
) {
    QiStatCardInternal(
        title = title,
        value = value,
        modifier = modifier,
        valueColor = valueColor,
        pressEnabled = pressEnabled
    )
}

@Composable
private fun QiStatCardInternal(
    title: String,
    value: String,
    modifier: Modifier,
    valueColor: Color,
    pressEnabled: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressEnabled && isPressed) 0.96f else 1f,
        animationSpec = tween(50),
        label = "statCardScale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .then(
                if (pressEnabled) Modifier.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            isPressed = event.changes.any { it.pressed }
                        }
                    }
                } else Modifier
            ),
        shape = RoundedCornerShape(QiRadius.xl),
        color = QiTheme.colors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = title,
                fontFamily = JakartaFontFamily,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                color = QiTheme.colors.TextTertiary
            )
            Text(
                text = value,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 20.sp,
                color = valueColor
            )
        }
    }
}

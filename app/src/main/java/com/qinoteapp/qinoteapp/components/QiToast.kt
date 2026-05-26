package com.qinoteapp.qinoteapp.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.ui.theme.QiToastMessage
import com.qinoteapp.qinoteapp.ui.theme.QiToastAction
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import kotlinx.coroutines.delay

data class QiToastData(
    val message: String,
    val type: QiToastType = QiToastType.Info,
    val durationMs: Long = 3000L,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)

enum class QiToastType {
    Info,
    Success,
    Error
}

@Composable
fun QiToastHost(
    toast: QiToastData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = QiTheme.colors
    AnimatedVisibility(
        visible = toast != null,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + scaleIn(
            initialScale = 0.88f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ),
        exit = fadeOut(
            animationSpec = spring(stiffness = Spring.StiffnessHigh)
        ) + scaleOut(
            targetScale = 0.92f,
            animationSpec = spring(stiffness = Spring.StiffnessHigh)
        ) + slideOutVertically(
            targetOffsetY = { it / 5 },
            animationSpec = spring(stiffness = Spring.StiffnessHigh)
        ),
        modifier = modifier
    ) {
        LaunchedEffect(toast) {
            if (toast != null) {
                delay(toast.durationMs)
                onDismiss()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(QiRadius.xl),
                        ambientColor = colors.Primary.copy(alpha = 0.15f),
                        spotColor = colors.Primary.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(QiRadius.xl))
                    .background(colors.Surface)
                    .padding(horizontal = Spacing.xl, vertical = Spacing.md)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = toast?.message ?: "",
                        color = colors.OnBackground,
                        style = QiToastMessage
                    )
                    if (toast?.actionLabel != null) {
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(QiRadius.sm))
                                .clickable {
                                    toast.onAction?.invoke()
                                    onDismiss()
                                }
                                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = toast.actionLabel,
                                style = QiToastAction,
                                color = colors.Primary
                            )
                        }
                    }
                }
            }
        }
    }
}

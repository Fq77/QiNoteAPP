package com.qinoteapp.qinoteapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qinoteapp.qinoteapp.ui.theme.JakartaFontFamily
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun QiEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = QiTheme.colors.TextQuaternary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = QiTheme.colors.TextTertiary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = QiTheme.colors.TextQuaternary
                )
            }
            if (actionText != null && onAction != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(QiRadius.lg))
                        .background(QiTheme.colors.Primary.copy(alpha = 0.1f))
                        .clickable(onClick = onAction)
                        .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = actionText,
                        fontFamily = JakartaFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        color = QiTheme.colors.Primary
                    )
                }
            }
        }
    }
}

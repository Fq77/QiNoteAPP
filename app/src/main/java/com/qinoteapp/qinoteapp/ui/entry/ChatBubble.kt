package com.qinoteapp.qinoteapp.ui.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.qinoteapp.qinoteapp.components.AiIcon
import com.qinoteapp.qinoteapp.components.BubbleIn
import com.qinoteapp.qinoteapp.components.PulseDot
import com.qinoteapp.qinoteapp.components.QiButton
import com.qinoteapp.qinoteapp.ui.theme.QiElevation
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun ChatBubble(
    message: ChatMessageData,
    isLatestFailed: Boolean = false,
    onSwitchToManual: () -> Unit = {},
    onRetry: () -> Unit = {},
    onImageClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == "user"
    val colors = QiTheme.colors
    val showErrorDialog = remember { mutableStateOf(false) }

    BubbleIn(visible = true) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.xs),
            verticalAlignment = Alignment.Top
        ) {
            if (!isUser) {
                AiIcon(iconSize = 16.dp, containerSize = 28.dp)
                Spacer(modifier = Modifier.width(Spacing.sm))
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                val maxBubbleWidth = (LocalConfiguration.current.screenWidthDp.dp - 80.dp)
                val bubbleShape = if (isUser) {
                    RoundedCornerShape(
                        topStart = QiRadius.lg,
                        topEnd = QiRadius.lg,
                        bottomStart = QiRadius.lg,
                        bottomEnd = QiRadius.xl
                    )
                } else {
                    RoundedCornerShape(
                        topStart = QiRadius.lg,
                        topEnd = QiRadius.lg,
                        bottomStart = QiRadius.xl,
                        bottomEnd = QiRadius.lg
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = maxBubbleWidth)
                            .clip(bubbleShape)
                            .background(if (isUser) colors.Primary else colors.Surface1)
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    ) {
                        if (message.isStatus) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PulseDot(
                                    color = colors.Accent,
                                    size = 5.dp
                                )
                                Spacer(modifier = Modifier.width(Spacing.sm))
                                Text(
                                    text = message.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.OnBackground
                                )
                            }
                        } else {
                            if (isUser && message.imageUri != null) {
                                val imageFile = java.io.File(message.imageUri)
                                val imageModel = if (imageFile.exists()) imageFile else message.imageUri
                                AsyncImage(
                                    model = imageModel,
                                    contentDescription = "上传的图片",
                                    modifier = Modifier
                                        .widthIn(max = 160.dp)
                                        .clip(RoundedCornerShape(QiRadius.md))
                                        .clickable { onImageClick?.invoke(message.imageUri) },
                                    contentScale = ContentScale.Fit
                                )
                                if (message.content.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(Spacing.xxs))
                                }
                            }
                            if (message.content.isNotBlank()) {
                                Text(
                                    text = message.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isUser) colors.OnPrimary else colors.OnBackground
                                )
                            }
                        }
                    }

                    if (message.isFailed && message.errorDetail != null) {
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "查看错误详情",
                            tint = colors.TextTertiary,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { showErrorDialog.value = true }
                        )
                    }
                }

                if (message.isFailed && isLatestFailed) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        QiButton.Secondary(
                            text = "重试",
                            onClick = onRetry,
                            icon = Icons.Default.Refresh
                        )
                        QiButton.Secondary(
                            text = "手动记账",
                            onClick = onSwitchToManual,
                            icon = Icons.Default.Edit
                        )
                    }
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(Spacing.sm))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(colors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "用户",
                        tint = colors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showErrorDialog.value) {
        Dialog(onDismissRequest = { showErrorDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(QiRadius.xl),
                color = colors.Surface,
                shadowElevation = QiElevation.md
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg)
                ) {
                    Text(
                        text = "错误详情",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.OnBackground
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = message.errorDetail ?: "未知错误",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.TextTertiary
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(QiRadius.md))
                            .background(colors.Primary)
                            .clickable { showErrorDialog.value = false }
                            .padding(vertical = Spacing.sm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "关闭",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.OnPrimary
                        )
                    }
                }
            }
        }
    }
}

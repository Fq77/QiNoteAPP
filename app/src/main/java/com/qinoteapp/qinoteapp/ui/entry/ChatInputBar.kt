package com.qinoteapp.qinoteapp.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import com.qinoteapp.qinoteapp.components.QiTextField
import com.qinoteapp.qinoteapp.components.qiPressScale
import com.qinoteapp.qinoteapp.ui.theme.QiRadius
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onImagePick: () -> Unit,
    isLoading: Boolean,
    hasImage: Boolean = false,
    onCancel: () -> Unit = {}
) {
    val colors = QiTheme.colors
    val canSend = (value.isNotBlank() || hasImage) && !isLoading

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        IconButton(
            onClick = onImagePick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = colors.Surface1,
                contentColor = colors.OnSurface
            ),
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "上传图片",
                modifier = Modifier.size(20.dp)
            )
        }

        QiTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            placeholder = {
                Text(
                    text = if (isLoading) "AI正在思考..." else "描述你的消费记录...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.TextTertiary
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.OnBackground),
            shape = RoundedCornerShape(QiRadius.xl),
            singleLine = false,
            maxLines = 2,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (canSend) onSend()
            })
        )

        val onClickAction = if (isLoading) onCancel else ({ if (canSend) onSend() })
        IconButton(
            onClick = onClickAction,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isLoading) colors.Expense else colors.Primary,
                contentColor = if (isLoading) Color.White else colors.OnPrimary,
                disabledContainerColor = colors.Surface2,
                disabledContentColor = colors.TextTertiary
            ),
            modifier = Modifier
                .size(44.dp)
                .qiPressScale()
        ) {
            Icon(
                imageVector = if (isLoading) Icons.Filled.Close else Icons.AutoMirrored.Filled.Send,
                contentDescription = if (isLoading) "终止" else "发送",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

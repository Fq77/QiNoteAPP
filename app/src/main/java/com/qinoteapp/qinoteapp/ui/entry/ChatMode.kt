package com.qinoteapp.qinoteapp.ui.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.qinoteapp.qinoteapp.components.QiImageViewer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qinoteapp.qinoteapp.ui.theme.QiTheme
import com.qinoteapp.qinoteapp.ui.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun ChatMode(
    viewModel: EntryViewModel,
    onImagePick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val previousMessageCount = remember { mutableStateOf(uiState.chatMessages.size) }
    var viewingImageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.chatMessages.size) {
        if (uiState.chatMessages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.chatMessages.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val colors = QiTheme.colors

                com.qinoteapp.qinoteapp.components.AiIcon(
                    iconSize = 32.dp,
                    containerSize = 56.dp
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                Text(
                    text = "描述你的消费，我来帮你记账",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.OnBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = "例如：午餐花了35元、打车回家18.5元",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.TextTertiary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                val examplePrompts = listOf("午餐花了35元", "打车回家18.5元", "超市购物128元")
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    examplePrompts.forEach { prompt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateInputText(prompt) },
                            shape = RoundedCornerShape(com.qinoteapp.qinoteapp.ui.theme.QiRadius.lg),
                            color = colors.Surface1
                        ) {
                            Text(
                                text = prompt,
                                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.OnSurface
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = Spacing.sm)
            ) {
                items(
                    items = uiState.chatMessages,
                    key = { it.id }
                ) { message ->
                    val isNew = message.id == uiState.chatMessages.lastOrNull()?.id &&
                            uiState.chatMessages.size > previousMessageCount.value

                    LaunchedEffect(message.id) {
                        previousMessageCount.value = uiState.chatMessages.size
                    }

                    val isLatestFailed = message.isFailed && message.id == uiState.chatMessages.lastOrNull { it.isFailed }?.id
                    ChatBubble(
                        message = message,
                        isLatestFailed = isLatestFailed,
                        onSwitchToManual = { viewModel.switchMode(1) },
                        onRetry = { viewModel.retryLastMessage() },
                        onImageClick = { uri -> viewingImageUri = uri }
                    )
                }
            }
        }

        ChatInputBar(
            value = uiState.inputText,
            onValueChange = { viewModel.updateInputText(it) },
            onSend = { viewModel.sendMessage() },
            onImagePick = onImagePick,
            isLoading = uiState.isLoading,
            hasImage = uiState.pendingImageBase64 != null,
            onCancel = { viewModel.cancelAiRequest() }
        )

        QiImageViewer(
            imageUri = viewingImageUri,
            onDismiss = { viewingImageUri = null }
        )
    }
}

package com.qinoteapp.qinoteapp.ui.entry

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import com.qinoteapp.qinoteapp.data.entity.ChatMessageEntity
import com.qinoteapp.qinoteapp.data.local.PreferencesManager
import com.qinoteapp.qinoteapp.data.repository.BillRepository
import com.qinoteapp.qinoteapp.data.repository.CategoryRepository
import com.qinoteapp.qinoteapp.data.repository.ChatMessageRepository
import com.qinoteapp.qinoteapp.network.ParsedBill
import com.qinoteapp.qinoteapp.usecase.AiBookkeepingUseCase
import com.qinoteapp.qinoteapp.util.AmountUtils
import com.qinoteapp.qinoteapp.notification.BookkeepingNotificationManager
import com.qinoteapp.qinoteapp.notification.BookkeepingState
import com.qinoteapp.qinoteapp.notification.BookkeepingNotificationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class ChatMessageData(
    val id: String,
    val role: String,
    val content: String,
    val imageUri: String? = null,
    val isStatus: Boolean = false,
    val isFailed: Boolean = false,
    val errorDetail: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val parsedBill: ParsedBill? = null,
    val billConfirmed: Boolean = false
)

fun ChatMessageData.toEntity(): ChatMessageEntity = ChatMessageEntity(
    id = id,
    role = role,
    content = content,
    imageUri = imageUri,
    isStatus = isStatus,
    isFailed = isFailed,
    errorDetail = errorDetail,
    timestamp = timestamp
)

fun ChatMessageEntity.toData(): ChatMessageData = ChatMessageData(
    id = id,
    role = role,
    content = content,
    imageUri = imageUri,
    isStatus = isStatus,
    isFailed = isFailed,
    errorDetail = errorDetail,
    timestamp = timestamp
)

data class ManualEntryState(
    val type: String = "expense",
    val selectedCategory: String = "",
    val title: String = "",
    val amount: String = "",
    val note: String = "",
    val date: String = LocalDate.now().toString(),
    val time: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
    val image: String? = null
)

data class EntryUiState(
    val mode: Int = 0,
    val chatMessages: List<ChatMessageData> = listOf(
        ChatMessageData(
            id = "welcome",
            role = "assistant",
            content = "你好！我是奇记AI助手\n直接告诉我花了什么，或者上传小票照片，我来帮你快速记账！\n\n例如：\n• 午餐花了35元\n• 打车去公司28.5元\n• 上传一张购物小票"
        )
    ),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val manualEntry: ManualEntryState = ManualEntryState(),
    val categories: List<CategoryEntity> = emptyList(),
    val pendingImageUri: Uri? = null,
    val pendingImageBase64: String? = null,
    val billJustAdded: Boolean = false
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val application: Application,
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val preferencesManager: PreferencesManager,
    private val aiBookkeepingUseCase: AiBookkeepingUseCase,
    private val notificationManager: BookkeepingNotificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    private var currentAiJob: Job? = null
    private var editingBillMessageId: String? = null

    private val welcomeMessage = ChatMessageData(
        id = "welcome",
        role = "assistant",
        content = "你好！我是奇记AI助手\n直接告诉我花了什么，或者上传小票照片，我来帮你快速记账！\n\n例如：\n• 午餐花了35元\n• 打车去公司28.5元\n• 上传一张购物小票"
    )

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            val saveChatHistory = preferencesManager.aiConfig.first().saveChatHistory
            if (saveChatHistory) {
                chatMessageRepository.getAllMessages().first().let { entities ->
                    if (entities.isNotEmpty()) {
                        val messages = entities.map { it.toData() }
                        _uiState.update { it.copy(chatMessages = messages) }
                    }
                }
            }
        }
        viewModelScope.launch {
            chatMessageRepository.getAllMessages().collect { entities ->
                if (entities.isEmpty() && _uiState.value.chatMessages.any { it.id != "welcome" }) {
                    _uiState.update { it.copy(chatMessages = listOf(welcomeMessage)) }
                }
            }
        }
    }

    private suspend fun copyImageToInternalStorage(sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = application.contentResolver.openInputStream(sourceUri) ?: return@withContext null
            val imageDir = File(application.filesDir, "chat_images")
            if (!imageDir.exists()) imageDir.mkdirs()
            val file = File(imageDir, "${UUID.randomUUID()}.jpg")
            file.outputStream().use { out -> inputStream.copyTo(out) }
            inputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            Log.w("EntryViewModel", "copyImageToInternalStorage failed: ${e.message}")
            null
        }
    }

    private suspend fun persistMessage(message: ChatMessageData) {
        val saveChatHistory = preferencesManager.aiConfig.first().saveChatHistory
        if (saveChatHistory && !message.isStatus) {
            chatMessageRepository.addMessage(message.toEntity())
        }
    }

    private suspend fun removePersistedMessages(ids: List<String>) {
        val saveChatHistory = preferencesManager.aiConfig.first().saveChatHistory
        if (saveChatHistory && ids.isNotEmpty()) {
            chatMessageRepository.deleteByIds(ids)
        }
    }

    private fun getCategoriesForPrompt(): List<Pair<String, String>> {
        return _uiState.value.categories.map { it.id to it.name }
    }

    fun switchMode(mode: Int) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun setImageUri(uri: Uri) {
        val currentMode = _uiState.value.mode
        _uiState.update { it.copy(pendingImageUri = uri) }
        viewModelScope.launch {
            val internalPath = copyImageToInternalStorage(uri)
            val base64 = aiBookkeepingUseCase.uriToBase64(uri)
            if (base64.isEmpty()) {
                _uiState.update { it.copy(pendingImageUri = null, pendingImageBase64 = null) }
                addStatusMessage("图片读取失败，请重新选择")
                return@launch
            }
            _uiState.update { it.copy(pendingImageBase64 = base64) }
            if (currentMode == 1) {
                _uiState.update { it.copy(
                    manualEntry = it.manualEntry.copy(image = internalPath)
                ) }
            } else {
                sendMessage(internalImagePath = internalPath)
            }
        }
    }

    fun clearImageUri() {
        _uiState.update { it.copy(pendingImageUri = null, pendingImageBase64 = null) }
    }

    private fun addStatusMessage(text: String) {
        val msg = ChatMessageData(
            id = UUID.randomUUID().toString(),
            role = "assistant",
            content = text,
            isStatus = true
        )
        _uiState.update { it.copy(chatMessages = it.chatMessages + msg) }
    }

    private fun removeLastStatusMessage() {
        _uiState.update { state ->
            if (state.chatMessages.isNotEmpty() && state.chatMessages.last().isStatus) {
                state.copy(chatMessages = state.chatMessages.dropLast(1))
            } else state
        }
    }

    fun sendMessage(internalImagePath: String? = null) {
        val currentState = _uiState.value
        val text = currentState.inputText.trim()
        if (text.isBlank() && currentState.pendingImageBase64 == null) return

        val imageBase64 = currentState.pendingImageBase64
        val imageUriStr = internalImagePath

        val userMessage = ChatMessageData(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = text,
            imageUri = imageUriStr
        )

        _uiState.update { it.copy(
            chatMessages = it.chatMessages + userMessage,
            inputText = "",
            isLoading = true,
            pendingImageUri = null,
            pendingImageBase64 = null
        ) }

        viewModelScope.launch {
            persistMessage(userMessage)
            currentAiJob = this.coroutineContext[Job]

            try {
                val aiConfig = preferencesManager.aiConfig.first()
                val maxRetries = if (aiConfig.autoRetry) 3 else 1
                val categories = getCategoriesForPrompt()

                addStatusMessage("解析中...")

                val result = aiBookkeepingUseCase.execute(
                    text = text,
                    imageBase64 = imageBase64,
                    imageUri = imageUriStr,
                    chatHistory = _uiState.value.chatMessages,
                    categories = categories,
                    maxRetries = maxRetries,
                    onRetry = { attempt ->
                        removeLastStatusMessage()
                        addStatusMessage("重试中（第${attempt}次）...")
                    },
                    onNotificationStateChanged = { state, data ->
                        notificationManager.showNotification(data ?: BookkeepingNotificationData(state = state))
                    }
                )
                Log.d("EntryViewModel", "AI result: billSaved=${result.billSaved}, parsedBill=${result.parsedBill}, time=${result.parsedBill?.time}")

                removeLastStatusMessage()
                finishAiRequest(result.message, result.parsedBill, result.billSaved)
            } catch (e: Exception) {
                Log.w("EntryViewModel", "sendMessage failed: ${e.message}")
                removeLastStatusMessage()
                val failMessage = ChatMessageData(
                    id = UUID.randomUUID().toString(),
                    role = "assistant",
                    content = "发生错误，请稍后再试",
                    isFailed = true,
                    errorDetail = e.message
                )
                finishAiRequest(failMessage, null, false)
            }
        }
    }

    fun retryLastMessage() {
        val messages = _uiState.value.chatMessages
        val lastFailedIndex = messages.indexOfLast { it.isFailed }
        if (lastFailedIndex == -1) return

        val failedMessage = messages[lastFailedIndex]
        val cleanedMessages = messages.filterIndexed { index, _ -> index != lastFailedIndex }

        _uiState.update { it.copy(chatMessages = cleanedMessages, isLoading = true) }

        viewModelScope.launch {
            removePersistedMessages(listOf(failedMessage.id))
            currentAiJob = this.coroutineContext[Job]

            var userMessageIndex = -1
            for (i in (cleanedMessages.size - 1) downTo 0) {
                if (cleanedMessages[i].role == "user" && !cleanedMessages[i].isStatus) {
                    userMessageIndex = i
                    break
                }
            }
            if (userMessageIndex == -1) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val userMessage = cleanedMessages[userMessageIndex]
            val userText = userMessage.content
            val userImageUri = userMessage.imageUri

            try {
                val imageBase64 = userImageUri?.let { path ->
                    withContext(Dispatchers.IO) {
                        val uri = if (path.startsWith("/") || path.startsWith("file:")) {
                            Uri.fromFile(File(path.removePrefix("file://")))
                        } else {
                            Uri.parse(path)
                        }
                        aiBookkeepingUseCase.uriToBase64(uri)
                    }
                } ?: ""

                val categories = getCategoriesForPrompt()

                addStatusMessage("解析中...")

                val result = aiBookkeepingUseCase.execute(
                    text = userText,
                    imageBase64 = imageBase64.ifEmpty { null },
                    imageUri = userImageUri,
                    chatHistory = cleanedMessages,
                    categories = categories,
                    maxRetries = 1,
                    onRetry = null,
                    onNotificationStateChanged = { state, data ->
                        notificationManager.showNotification(data ?: BookkeepingNotificationData(state = state))
                    }
                )

                removeLastStatusMessage()
                finishAiRequest(result.message, result.parsedBill, result.billSaved)
            } catch (e: Exception) {
                Log.w("EntryViewModel", "retryLastMessage failed: ${e.message}")
                removeLastStatusMessage()
                val failMessage = ChatMessageData(
                    id = UUID.randomUUID().toString(),
                    role = "assistant",
                    content = "重试失败，请稍后再试或使用手动记账",
                    isFailed = true,
                    errorDetail = e.message
                )
                finishAiRequest(failMessage, null, false)
            }
        }
    }

    private suspend fun finishAiRequest(message: ChatMessageData, parsedBill: ParsedBill?, billSaved: Boolean) {
        _uiState.update { it.copy(
            chatMessages = it.chatMessages + message,
            isLoading = false,
            billJustAdded = if (billSaved) true else it.billJustAdded
        ) }
        persistMessage(message)
    }

    fun cancelAiRequest() {
        currentAiJob?.cancel()
        currentAiJob = null
        removeLastStatusMessage()
        _uiState.update { it.copy(isLoading = false) }
    }

    fun submitManualEntry() {
        val entry = _uiState.value.manualEntry
        val amountCents = AmountUtils.yuanToCents(entry.amount)
        if (amountCents == null || amountCents <= 0L) return
        if (entry.title.isBlank()) return
        if (entry.selectedCategory.isBlank()) return

        viewModelScope.launch {
            val existing = billRepository.findDuplicate(
                title = entry.title,
                amount = amountCents,
                type = entry.type,
                date = entry.date,
                time = entry.time
            )
            if (existing != null) {
                addStatusMessage("⚠ 检测到重复记录：${entry.title} ¥${entry.amount}，已跳过")
                return@launch
            }

            val imagePath = entry.image
            val finalImagePath = if (!imagePath.isNullOrBlank() && imagePath.startsWith("content://")) {
                copyImageToInternalStorage(Uri.parse(imagePath))
            } else {
                imagePath
            }
            val bill = BillEntity(
                id = UUID.randomUUID().toString(),
                type = entry.type,
                category = entry.selectedCategory,
                title = entry.title,
                amount = amountCents,
                note = entry.note,
                date = entry.date,
                time = entry.time,
                image = finalImagePath
            )
            billRepository.addBill(bill)

            val msgId = editingBillMessageId
            if (msgId != null) {
                _uiState.update { state ->
                    val newMessages = state.chatMessages.map { msg ->
                        if (msg.id == msgId) {
                            msg.copy(billConfirmed = true, content = "✓ 已通过手动修改记录")
                        } else msg
                    }
                    state.copy(
                        chatMessages = newMessages,
                        manualEntry = ManualEntryState(),
                        billJustAdded = true
                    )
                }
                editingBillMessageId = null
            } else {
                _uiState.update { it.copy(
                    manualEntry = ManualEntryState(),
                    billJustAdded = true
                ) }
            }
        }
    }

    fun updateManualEntry(update: (ManualEntryState) -> ManualEntryState) {
        _uiState.update { it.copy(manualEntry = update(it.manualEntry)) }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun resetBillAdded() {
        _uiState.update { it.copy(billJustAdded = false) }
    }
}

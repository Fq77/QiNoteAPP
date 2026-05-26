package com.qinoteapp.qinoteapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.entity.CategoryEntity
import com.qinoteapp.qinoteapp.data.local.PreferencesManager
import com.qinoteapp.qinoteapp.data.local.AiConfig
import com.qinoteapp.qinoteapp.data.local.AppConfig
import com.qinoteapp.qinoteapp.data.repository.BillRepository
import com.qinoteapp.qinoteapp.data.repository.CategoryRepository
import com.qinoteapp.qinoteapp.data.repository.ChatMessageRepository
import com.qinoteapp.qinoteapp.network.AiApiService
import com.qinoteapp.qinoteapp.network.ChatMessage
import com.qinoteapp.qinoteapp.util.AmountUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val aiConfig: AiConfig = AiConfig(),
    val appConfig: AppConfig = AppConfig(),
    val categories: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val billRepository: BillRepository,
    private val chatMessageRepository: ChatMessageRepository,
    val preferencesManager: PreferencesManager,
    private val aiApiService: AiApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.aiConfig.collect { config ->
                _uiState.update { it.copy(aiConfig = config) }
            }
        }
        viewModelScope.launch {
            preferencesManager.appConfig.collect { config ->
                _uiState.update { it.copy(appConfig = config) }
            }
        }
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun updateAiConfig(config: AiConfig) {
        viewModelScope.launch {
            preferencesManager.updateAiConfig(config)
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            val currentConfig = _uiState.value.appConfig
            preferencesManager.updateAppConfig(currentConfig.copy(themeMode = mode))
        }
    }

    fun updatePredictiveBack(enabled: Boolean) {
        viewModelScope.launch {
            val currentConfig = _uiState.value.appConfig
            preferencesManager.updateAppConfig(currentConfig.copy(predictiveBack = enabled))
        }
    }

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.addCategory(category)
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }

    suspend fun testConnection(apiAddress: String, apiKey: String, model: String): Result<String> {
        return try {
            val result = aiApiService.sendChatMessage(
                apiAddress = apiAddress,
                apiKey = apiKey,
                model = model,
                messages = listOf(ChatMessage(role = "user", content = "你好"))
            )
            result.fold(
                onSuccess = { Result.success("连接成功") },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearAllBills() {
        viewModelScope.launch {
            billRepository.deleteAllBills()
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatMessageRepository.deleteAllMessages()
        }
    }

    suspend fun exportData(format: String): Result<String> {
        val bills = billRepository.getAllBills().first()
        return when (format) {
            "csv" -> {
                try {
                    val sb = StringBuilder()
                    sb.appendLine("\uFEFF类型,标题,分类,金额,备注,日期,时间")
                    bills.forEach { bill ->
                        sb.appendLine("${bill.type},${bill.title},${bill.category},${AmountUtils.formatCents(bill.amount)},${bill.note},${bill.date},${bill.time}")
                    }
                    Result.success(sb.toString())
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
            else -> {
                billRepository.exportBillsToJson(bills)
            }
        }
    }

    suspend fun importData(jsonString: String): Result<Unit> {
        val importResult = billRepository.importBillsFromJson(jsonString)
        return importResult.fold(
            onSuccess = { bills ->
                var failed = false
                bills.forEach { bill ->
                    val addResult = billRepository.addBill(bill)
                    if (addResult.isFailure) failed = true
                }
                if (failed) Result.failure(Exception("部分账单导入失败")) else Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    fun updateSaveChatHistory(enabled: Boolean) {
        viewModelScope.launch {
            val currentConfig = _uiState.value.aiConfig
            preferencesManager.updateAiConfig(currentConfig.copy(saveChatHistory = enabled))
        }
    }
}

package com.qinoteapp.qinoteapp.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinoteapp.qinoteapp.components.QiToastData
import com.qinoteapp.qinoteapp.components.QiToastType
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.local.PreferencesManager
import com.qinoteapp.qinoteapp.data.repository.BillRepository
import com.qinoteapp.qinoteapp.data.repository.CategoryRepository
import com.qinoteapp.qinoteapp.network.ParsedBill
import com.qinoteapp.qinoteapp.usecase.AiBookkeepingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class UiState(
    val bills: List<BillEntity> = emptyList(),
    val filteredBills: List<BillEntity> = emptyList(),
    val todayIncome: Long = 0L,
    val todayExpense: Long = 0L,
    val todayBalance: Long = 0L,
    val monthIncome: Long = 0L,
    val monthExpense: Long = 0L,
    val monthBalance: Long = 0L,
    val isLoading: Boolean = true,
    val categories: Map<String, Pair<String, String>> = emptyMap(),
    val searchQuery: String = "",
    val isSearchExpanded: Boolean = false
)

data class ReAnalyzeState(
    val isLoading: Boolean = false,
    val newParsedBill: ParsedBill? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository,
    private val aiApiService: com.qinoteapp.qinoteapp.network.AiApiService,
    private val preferencesManager: PreferencesManager,
    private val aiBookkeepingUseCase: AiBookkeepingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _reAnalyzeState = MutableStateFlow(ReAnalyzeState())
    val reAnalyzeState: StateFlow<ReAnalyzeState> = _reAnalyzeState.asStateFlow()

    private val _toastMessage = MutableStateFlow<QiToastData?>(null)
    val toastMessage: StateFlow<QiToastData?> = _toastMessage.asStateFlow()

    fun showToast(message: String) {
        _toastMessage.value = QiToastData(message = message)
    }

    fun showToast(toast: QiToastData) {
        _toastMessage.value = toast
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    init {
        loadCategories()
        observeBills()
        observeStats()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                val categoryMap = categories.associate {
                    it.id to Pair(it.name, it.icon)
                }
                _uiState.update { it.copy(categories = categoryMap) }
                updateFilteredBills()
            }
        }
    }

    private fun updateFilteredBills() {
        val state = _uiState.value
        val query = state.searchQuery.trim()
        val filtered = if (query.isBlank()) {
            state.bills
        } else {
            state.bills.filter { bill ->
                val categoryName = state.categories[bill.category]?.first ?: ""
                bill.title.contains(query, ignoreCase = true) ||
                    bill.note.contains(query, ignoreCase = true) ||
                    categoryName.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(filteredBills = filtered) }
    }

    private fun observeBills() {
        viewModelScope.launch {
            billRepository.getAllBills().collect { bills ->
                _uiState.update {
                    it.copy(
                        bills = bills,
                        filteredBills = if (it.searchQuery.isBlank()) bills else bills.filter { bill ->
                            val categoryName = it.categories[bill.category]?.first ?: ""
                            bill.title.contains(it.searchQuery, ignoreCase = true) ||
                                bill.note.contains(it.searchQuery, ignoreCase = true) ||
                                categoryName.contains(it.searchQuery, ignoreCase = true)
                        },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            combine(
                billRepository.getDayIncome(getTodayDateString()),
                billRepository.getDayExpense(getTodayDateString()),
                billRepository.getMonthIncome(getCurrentMonthPrefix()),
                billRepository.getMonthExpense(getCurrentMonthPrefix())
            ) { dayIn, dayOut, monthIn, monthOut ->
                _uiState.update {
                    it.copy(
                        todayIncome = dayIn,
                        todayExpense = dayOut,
                        todayBalance = dayIn - dayOut,
                        monthIncome = monthIn,
                        monthExpense = monthOut,
                        monthBalance = monthIn - monthOut
                    )
                }
            }.collect { }
        }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch {
            billRepository.deleteBill(bill)
            _toastMessage.value = QiToastData(
                message = "已删除",
                type = QiToastType.Info,
                durationMs = 2000L
            )
        }
    }

    fun updateBill(bill: BillEntity) {
        viewModelScope.launch {
            billRepository.updateBill(bill)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateFilteredBills()
    }

    fun toggleSearch() {
        _uiState.update {
            it.copy(
                isSearchExpanded = !it.isSearchExpanded,
                searchQuery = ""
            )
        }
    }

    fun reAnalyzeBill(bill: BillEntity) {
        viewModelScope.launch {
            _reAnalyzeState.update { ReAnalyzeState(isLoading = true) }
            try {
                val aiConfig = preferencesManager.aiConfig.first()
                if (!aiConfig.aiEnabled || aiConfig.apiKey.isBlank()) {
                    _reAnalyzeState.update { ReAnalyzeState(error = "AI功能未启用或未配置API密钥") }
                    return@launch
                }

                val imageBase64 = if (!bill.image.isNullOrBlank()) {
                    val imageUri = when {
                        java.io.File(bill.image).exists() -> Uri.fromFile(java.io.File(bill.image))
                        bill.image.startsWith("content://") -> Uri.parse(bill.image)
                        bill.image.startsWith("file://") -> Uri.parse(bill.image)
                        else -> Uri.fromFile(java.io.File(bill.image))
                    }
                    aiBookkeepingUseCase.uriToBase64(imageUri)
                } else {
                    ""
                }

                val categories = _uiState.value.categories.entries.map { (id, pair) ->
                    id to pair.first
                }

                val result = if (imageBase64.isNotEmpty()) {
                    aiApiService.sendVisionMessage(
                        apiAddress = aiConfig.apiAddress,
                        apiKey = aiConfig.apiKey,
                        model = aiConfig.visionModel,
                        text = "请重新分析这张账单图片",
                        imageBase64 = imageBase64,
                        customCategories = categories
                    )
                } else {
                    aiApiService.sendChatMessage(
                        apiAddress = aiConfig.apiAddress,
                        apiKey = aiConfig.apiKey,
                        model = aiConfig.textModel,
                        messages = listOf(
                            com.qinoteapp.qinoteapp.network.ChatMessage(
                                role = "user",
                                content = "请重新分析：${bill.title} ${com.qinoteapp.qinoteapp.util.AmountUtils.formatCents(bill.amount)}元"
                            )
                        ),
                        customCategories = categories
                    )
                }

                result.fold(
                    onSuccess = { response ->
                        if (response.parsedBill != null) {
                            _reAnalyzeState.update { ReAnalyzeState(newParsedBill = response.parsedBill) }
                        } else {
                            _reAnalyzeState.update { ReAnalyzeState(error = "AI未能识别出有效账单信息") }
                        }
                    },
                    onFailure = { error ->
                        _reAnalyzeState.update { ReAnalyzeState(error = error.message ?: "分析失败，请重试") }
                    }
                )
            } catch (e: Exception) {
                _reAnalyzeState.update { ReAnalyzeState(error = e.message ?: "分析失败，请重试") }
            }
        }
    }

    fun resetReAnalyze() {
        _reAnalyzeState.update { ReAnalyzeState() }
    }

    private fun getTodayDateString(): String = LocalDate.now().toString()
    private fun getCurrentMonthPrefix(): String = "${LocalDate.now().year}-${String.format("%02d", LocalDate.now().monthValue)}"
}

package com.qinoteapp.qinoteapp.usecase

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.qinoteapp.qinoteapp.data.entity.BillEntity
import com.qinoteapp.qinoteapp.data.local.PreferencesManager
import com.qinoteapp.qinoteapp.data.repository.BillRepository
import com.qinoteapp.qinoteapp.network.AiApiService
import com.qinoteapp.qinoteapp.network.ChatMessage
import com.qinoteapp.qinoteapp.network.ParsedBill
import com.qinoteapp.qinoteapp.network.localParseBill

import com.qinoteapp.qinoteapp.ui.entry.ChatMessageData
import com.qinoteapp.qinoteapp.util.AmountUtils
import com.qinoteapp.qinoteapp.notification.BookkeepingState
import com.qinoteapp.qinoteapp.notification.BookkeepingNotificationData
import com.qinoteapp.qinoteapp.notification.CategoryNameMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class AiBookkeepingResult(
    val message: ChatMessageData,
    val parsedBill: ParsedBill? = null,
    val billSaved: Boolean = false,
    val isDuplicate: Boolean = false
)

@Singleton
class AiBookkeepingUseCase @Inject constructor(
    private val aiApiService: AiApiService,
    private val billRepository: BillRepository,
    private val preferencesManager: PreferencesManager,
    private val contentResolver: ContentResolver
) {
    private companion object {
        const val TAG = "AiBookkeeping"
    }

    suspend fun execute(
        text: String,
        imageBase64: String?,
        imageUri: String?,
        chatHistory: List<ChatMessageData>,
        categories: List<Pair<String, String>>,
        maxRetries: Int = 3,
        onRetry: ((Int) -> Unit)? = null,
        onNotificationStateChanged: ((BookkeepingState, BookkeepingNotificationData?) -> Unit)? = null
    ): AiBookkeepingResult {
        onNotificationStateChanged?.invoke(BookkeepingState.PARSING, BookkeepingNotificationData(state = BookkeepingState.PARSING, title = text.take(20)))
        val aiConfig = preferencesManager.aiConfig.first()
        val isImageMessage = !imageBase64.isNullOrEmpty()
        val aiAvailable = aiConfig.aiEnabled && aiConfig.apiKey.isNotBlank()

        if (isImageMessage && aiAvailable) {
            return executeAiParse(
                text = text,
                imageBase64 = imageBase64,
                imageUri = imageUri,
                chatHistory = chatHistory,
                categories = categories,
                aiConfig = aiConfig,
                maxRetries = maxRetries,
                onRetry = onRetry,
                onNotificationStateChanged = onNotificationStateChanged
            )
        }

        val localResult = localParseBill(text)
        val localHighConfidence = localResult != null && localResult.category != "daily"

        if (localHighConfidence) {
            return handleParsedBill(localResult, imageUri, fromLocal = true, onNotificationStateChanged = onNotificationStateChanged)
        }

        if (aiAvailable) {
            return executeAiParse(
                text = text,
                imageBase64 = null,
                imageUri = imageUri,
                chatHistory = chatHistory,
                categories = categories,
                aiConfig = aiConfig,
                maxRetries = maxRetries,
                onRetry = onRetry,
                onNotificationStateChanged = onNotificationStateChanged
            )
        }

        if (localResult != null) {
            return handleParsedBill(localResult, imageUri, fromLocal = true, onNotificationStateChanged = onNotificationStateChanged)
        }

        return AiBookkeepingResult(
            message = ChatMessageData(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = "未能识别出消费信息，请尝试更详细的描述，例如「午餐花了35元」",
                isFailed = true,
                errorDetail = "本地正则解析失败且AI不可用"
            )
        )
    }

    private suspend fun executeAiParse(
        text: String,
        imageBase64: String?,
        imageUri: String?,
        chatHistory: List<ChatMessageData>,
        categories: List<Pair<String, String>>,
        aiConfig: com.qinoteapp.qinoteapp.data.local.AiConfig,
        maxRetries: Int,
        onRetry: ((Int) -> Unit)?,
        onNotificationStateChanged: ((BookkeepingState, BookkeepingNotificationData?) -> Unit)? = null
    ): AiBookkeepingResult {
        val historyMessages = chatHistory
            .filter { !it.isStatus && it.content.isNotBlank() }
            .takeLast(10)
            .map { ChatMessage(role = it.role, content = it.content) }

        val result = if (!imageBase64.isNullOrEmpty()) {
            val recentHistory = historyMessages.takeLast(2)
            aiApiService.sendVisionMessage(
                apiAddress = aiConfig.apiAddress,
                apiKey = aiConfig.apiKey,
                model = aiConfig.visionModel,
                text = text,
                imageBase64 = imageBase64,
                chatHistory = recentHistory,
                maxRetries = maxRetries,
                customCategories = categories,
                onRetry = onRetry
            )
        } else {
            aiApiService.sendChatMessage(
                apiAddress = aiConfig.apiAddress,
                apiKey = aiConfig.apiKey,
                model = aiConfig.textModel,
                messages = historyMessages,
                maxRetries = maxRetries,
                customCategories = categories,
                onRetry = onRetry
            )
        }

        return result.fold(
            onSuccess = { response ->
                if (response.parsedBill != null) {
                    handleParsedBill(response.parsedBill, imageUri, fromLocal = false, onNotificationStateChanged = onNotificationStateChanged)
                } else {
                    val localResult = localParseBill(text)
                    if (localResult != null) {
                        return handleParsedBill(localResult, imageUri, fromLocal = true, onNotificationStateChanged = onNotificationStateChanged)
                    }
                    AiBookkeepingResult(
                        message = ChatMessageData(
                            id = UUID.randomUUID().toString(),
                            role = "assistant",
                            content = "AI未能识别出有效账单信息，请尝试更详细的描述或使用手动记账",
                            isFailed = true,
                            errorDetail = "JSON解析失败"
                        )
                    )
                }
            },
            onFailure = { error ->
                handleAiFailure(error, text, imageUri, onNotificationStateChanged = onNotificationStateChanged)
            }
        )
    }

    private suspend fun handleAiFailure(
        error: Throwable,
        text: String,
        imageUri: String?,
        onNotificationStateChanged: ((BookkeepingState, BookkeepingNotificationData?) -> Unit)? = null
    ): AiBookkeepingResult {
        val localResult = localParseBill(text)
        if (localResult != null) {
            val result = handleParsedBill(localResult, imageUri, fromLocal = true, onNotificationStateChanged = onNotificationStateChanged)
            val localContent = if (result.parsedBill != null) {
                "AI解析失败，已通过本地识别\n请确认以下信息是否正确"
            } else {
                result.message.content
            }
            return result.copy(
                message = result.message.copy(content = localContent)
            )
        }

        onNotificationStateChanged?.invoke(
            BookkeepingState.FAILED,
            BookkeepingNotificationData(
                state = BookkeepingState.FAILED,
                errorMessage = error.message?.take(50)
            )
        )

        val errorMsg = classifyError(error.message)
        return AiBookkeepingResult(
            message = ChatMessageData(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = "AI解析失败：$errorMsg\n请稍后再试或使用手动记账",
                isFailed = true,
                errorDetail = error.message
            )
        )
    }

    private fun classifyError(message: String?): String {
        if (message == null) return "未知错误"
        return when {
            message.contains("401") || message.contains("Unauthorized") -> "API Key无效，请检查设置"
            message.contains("402") || message.contains("quota") || message.contains("余额") -> "API余额不足，请充值"
            message.contains("429") || message.contains("rate") -> "请求过于频繁，请稍后再试"
            message.contains("timeout", ignoreCase = true) || message.contains("SocketTimeout") -> "网络超时，请检查网络连接"
            message.contains("Unable to resolve host") || message.contains("UnknownHost") -> "网络不可用，请检查网络连接"
            message.contains("SSL") || message.contains("certificate") -> "网络连接异常，请检查VPN设置"
            else -> "网络请求失败（${message.take(30)}）"
        }
    }

    private fun parseTimeToHHMMSS(timeStr: String): String {
        val normalized = timeStr.trim()
            .replace("：", ":")
            .replace("．", ".")
            .replace("。", ".")
            .replace("，", ",")

        Log.d(TAG, "parseTimeToHHMMSS: input='$timeStr', normalized='$normalized'")

        val timeFormatters = listOf(
            DateTimeFormatter.ofPattern("HH:mm:ss"),
            DateTimeFormatter.ofPattern("H:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("H:mm:ss.SSS")
        )

        for (formatter in timeFormatters) {
            try {
                val parsed = LocalTime.parse(normalized, formatter)
                val result = parsed.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                Log.d(TAG, "parseTimeToHHMMSS: parsed with formatter, result='$result'")
                return result
            } catch (_: Exception) { continue }
        }

        val amPmRegex = Regex("""(\d{1,2}):(\d{2})\s*(am|pm|AM|PM)""")
        val amPmMatch = amPmRegex.find(normalized)
        if (amPmMatch != null) {
            val hour = amPmMatch.groupValues[1].toIntOrNull() ?: return ""
            val minute = amPmMatch.groupValues[2].toIntOrNull() ?: return ""
            val isPM = amPmMatch.groupValues[3].lowercase() == "pm"
            val adjustedHour = when {
                isPM && hour < 12 -> hour + 12
                !isPM && hour == 12 -> 0
                else -> hour
            }
            if (adjustedHour in 0..23 && minute in 0..59) {
                val result = String.format("%02d:%02d:00", adjustedHour, minute)
                Log.d(TAG, "parseTimeToHHMMSS: parsed AM/PM, result='$result'")
                return result
            }
        }

        val chineseTimePatterns = listOf(
            Regex("""(?:下午|傍晚)(\d{1,2})[点时:](\d{1,2})?[分]?""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                val minute = m.groupValues[2].toIntOrNull() ?: 0
                if (hour in 1..12) String.format("%02d:%02d:00", hour + 12, minute) else ""
            },
            Regex("""(?:下午|傍晚)(\d{1,2})[点时点半]""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                if (hour in 1..12) String.format("%02d:00:00", hour + 12) else ""
            },
            Regex("""(?:上午|早上|早晨|凌晨)(\d{1,2})[点时:](\d{1,2})?[分]?""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                val minute = m.groupValues[2].toIntOrNull() ?: 0
                if (hour in 0..12) String.format("%02d:%02d:00", hour, minute) else ""
            },
            Regex("""(?:上午|早上|早晨|凌晨)(\d{1,2})[点时点半]""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                if (hour in 0..12) String.format("%02d:00:00", hour) else ""
            },
            Regex("""中午(\d{1,2})[点时:](\d{1,2})?[分]?""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                val minute = m.groupValues[2].toIntOrNull() ?: 0
                if (hour in 10..12) String.format("%02d:%02d:00", hour, minute) else ""
            },
            Regex("""晚上(\d{1,2})[点时:](\d{1,2})?[分]?""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                val minute = m.groupValues[2].toIntOrNull() ?: 0
                if (hour in 1..12) String.format("%02d:%02d:00", hour + 12, minute.coerceAtMost(59)) else ""
            },
            Regex("""晚上(\d{1,2})[点时点半]""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                if (hour in 1..12) String.format("%02d:00:00", hour + 12) else ""
            },
            Regex("""(\d{1,2})[点时:](\d{1,2})[分]?""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                val minute = m.groupValues[2].toIntOrNull() ?: 0
                if (hour in 0..23 && minute in 0..59) String.format("%02d:%02d:00", hour, minute) else ""
            },
            Regex("""(\d{1,2})[点时点半]""") to { m: MatchResult ->
                val hour = m.groupValues[1].toIntOrNull() ?: return@to ""
                if (hour in 0..23) String.format("%02d:00:00", hour) else ""
            }
        )

        for ((pattern, converter) in chineseTimePatterns) {
            val match = pattern.find(normalized)
            if (match != null) {
                val result = converter(match)
                if (result.isNotBlank()) {
                    Log.d(TAG, "parseTimeToHHMMSS: parsed Chinese time, result='$result'")
                    return result
                }
            }
        }

        val digitOnlyRegex = Regex("""^(\d{1,2})(\d{2})$""")
        val digitMatch = digitOnlyRegex.find(normalized.replace(":", ""))
        if (digitMatch != null) {
            val hour = digitMatch.groupValues[1].toIntOrNull() ?: return ""
            val minute = digitMatch.groupValues[2].toIntOrNull() ?: return ""
            if (hour in 0..23 && minute in 0..59) {
                val result = String.format("%02d:%02d:00", hour, minute)
                Log.d(TAG, "parseTimeToHHMMSS: parsed digit-only, result='$result'")
                return result
            }
        }

        Log.w(TAG, "parseTimeToHHMMSS: failed to parse '$timeStr'")
        return ""
    }

    private suspend fun handleParsedBill(
        parsedBill: ParsedBill,
        imageUri: String?,
        fromLocal: Boolean,
        onNotificationStateChanged: ((BookkeepingState, BookkeepingNotificationData?) -> Unit)? = null
    ): AiBookkeepingResult {
        Log.d(TAG, "handleParsedBill: title=${parsedBill.title}, date=${parsedBill.date}, time=${parsedBill.time}, amount=${parsedBill.amount}, fromLocal=$fromLocal")
        val resolvedBill = resolveRelativeDates(parsedBill)
        Log.d(TAG, "handleParsedBill after resolve: date=${resolvedBill.date}, time=${resolvedBill.time}")

        val saved = confirmBill(resolvedBill, imageUri)

        val isDuplicate = saved == null
        val actualSaved = saved == true

        onNotificationStateChanged?.invoke(
            if (actualSaved) BookkeepingState.SUCCESS else BookkeepingState.FAILED,
            BookkeepingNotificationData(
                state = if (actualSaved) BookkeepingState.SUCCESS else BookkeepingState.FAILED,
                title = resolvedBill.title,
                amount = com.qinoteapp.qinoteapp.util.AmountUtils.formatCents(resolvedBill.amount),
                category = resolvedBill.category,
                categoryName = CategoryNameMap.getCategoryName(resolvedBill.category),
                categoryIconName = resolvedBill.category,
                type = resolvedBill.type,
                note = resolvedBill.note,
                errorMessage = if (isDuplicate) "重复记录" else if (!actualSaved) "保存失败" else null
            )
        )

        val sourceLabel = if (fromLocal) "本地识别" else "AI识别"
        val content = when {
            isDuplicate -> "⚠ 检测到重复记录\n该笔消费已存在于 ${resolvedBill.date} ${resolvedBill.time}\n标题：${resolvedBill.title}，金额：¥${AmountUtils.formatCents(resolvedBill.amount)}\n如需修改请手动编辑已有记录"
            actualSaved -> buildSuccessMessage(resolvedBill)
            else -> "${sourceLabel}完成，但保存失败，请重试"
        }

        return AiBookkeepingResult(
            message = ChatMessageData(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = content,
                imageUri = imageUri,
                parsedBill = resolvedBill,
                billConfirmed = actualSaved
            ),
            parsedBill = resolvedBill,
            billSaved = actualSaved,
            isDuplicate = isDuplicate
        )
    }

    private fun resolveRelativeDates(bill: ParsedBill): ParsedBill {
        val today = LocalDate.now()
        var resolvedDate = bill.date

        if (bill.date.isBlank()) {
            resolvedDate = today.toString()
        } else {
            val relativeMap = mapOf(
                "今天" to today,
                "昨天" to today.minusDays(1),
                "前天" to today.minusDays(2),
                "大前天" to today.minusDays(3)
            )
            relativeMap.forEach { (keyword, date) ->
                if (bill.date == keyword) {
                    resolvedDate = date.toString()
                }
            }
        }

        return bill.copy(date = resolvedDate)
    }

    fun buildSuccessMessage(parsedBill: ParsedBill): String {
        val typeLabel = if (parsedBill.type == "income") "收入" else "支出"
        val dateStr = if (parsedBill.date.isNotBlank()) parsedBill.date else LocalDate.now().toString()
        val timeStr = if (parsedBill.time.isNotBlank()) "\n时间：${parsedBill.time}" else ""
        val noteStr = if (parsedBill.note.isNotBlank()) "\n备注：${parsedBill.note}" else ""
        return "✓ 已记录${typeLabel}\n标题：${parsedBill.title}\n金额：¥${AmountUtils.formatCents(parsedBill.amount)}\n日期：$dateStr$timeStr$noteStr"
    }

    suspend fun confirmBill(parsedBill: ParsedBill, imageUri: String? = null): Boolean? {
        Log.d(TAG, "confirmBill: title=${parsedBill.title}, date=${parsedBill.date}, time='${parsedBill.time}'")
        val today = LocalDate.now()

        var extractedDate = parsedBill.date
        var extractedTime = parsedBill.time

        if (extractedDate.isNotBlank()) {
            val separators = listOf(" ", "T")
            var separated = false
            for (sep in separators) {
                if (extractedDate.contains(sep)) {
                    val parts = extractedDate.split(sep, limit = 2)
                    extractedDate = parts[0]
                    if (extractedTime.isBlank() && parts.size > 1) {
                        extractedTime = parts[1]
                        Log.d(TAG, "confirmBill: extracted time from date field: '$extractedTime'")
                    }
                    separated = true
                    break
                }
            }
        }

        if (extractedDate.isNotBlank()) {
            val normalizedDate = extractedDate.replace("/", "-")
            if (normalizedDate != extractedDate) {
                Log.d(TAG, "confirmBill: normalized date slashes to dashes: '$extractedDate' -> '$normalizedDate'")
                extractedDate = normalizedDate
            }
        }

        val billDate = try {
            if (extractedDate.isNotBlank()) LocalDate.parse(extractedDate) else today
        } catch (_: Exception) { today }

        val billTime = if (extractedTime.isNotBlank()) {
            parseTimeToHHMMSS(extractedTime)
        } else { "" }

        val existing = billRepository.findDuplicate(
            title = parsedBill.title,
            amount = parsedBill.amount,
            type = parsedBill.type,
            date = billDate.toString(),
            time = billTime
        )
        if (existing != null) {
            Log.d(TAG, "confirmBill: duplicate found, id=${existing.id}")
            return null
        }

        val bill = BillEntity(
            id = UUID.randomUUID().toString(),
            type = parsedBill.type,
            category = parsedBill.category,
            title = parsedBill.title,
            amount = parsedBill.amount,
            note = parsedBill.note,
            date = billDate.toString(),
            time = billTime,
            image = imageUri,
            source = "ai"
        )
        Log.d(TAG, "confirmBill: BillEntity time='${bill.time}', date='${bill.date}'")
        val saved = billRepository.addBill(bill).isSuccess
        return saved
    }

    suspend fun uriToBase64(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val uriString = uri.toString()
            val inputStream = if (uriString.startsWith("/") || uri.scheme == "file") {
                val path = uri.path ?: uriString.removePrefix("file://")
                val file = File(path)
                if (file.exists()) file.inputStream() else return@withContext ""
            } else {
                contentResolver.openInputStream(uri) ?: return@withContext ""
            }
            val bitmap = BitmapFactory.decodeStream(inputStream) ?: run {
                inputStream.close()
                return@withContext ""
            }
            inputStream.close()

            var width = bitmap.width
            var height = bitmap.height
            val maxSize = 800
            if (width > maxSize || height > maxSize) {
                val scale = maxSize.toFloat() / maxOf(width, height)
                width = (width * scale).toInt()
                height = (height * scale).toInt()
            }
            val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.w(TAG, "uriToBase64 failed: ${e.message}")
            ""
        }
    }
}

package com.qinoteapp.qinoteapp.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class AiResponse(
    val content: String,
    val parsedBill: ParsedBill? = null
)

@Serializable
data class ParsedBill(
    val type: String,
    val title: String,
    val category: String,
    val amount: Long,
    val note: String,
    val date: String = "",
    val time: String = ""
)

@Singleton
class AiApiService @Inject constructor(
    private val client: OkHttpClient
) {

    private val json = Json { ignoreUnknownKeys = true }

    private val builtinExpenseCategories = listOf(
        "food" to "餐饮美食", "transport" to "交通出行", "shopping" to "购物消费",
        "entertainment" to "休闲娱乐", "living" to "居住生活", "daily" to "日常杂项",
        "health" to "医疗健康", "education" to "教育培训", "social" to "人情社交",
        "pet" to "宠物萌宠", "invest_expense" to "投资理财", "digital" to "数码电子",
        "housing" to "住房缴费"
    )

    private val builtinIncomeCategories = listOf(
        "salary" to "工资收入", "freelance" to "兼职收入", "investment" to "投资收益"
    )

    private fun buildSystemPrompt(customCategories: List<Pair<String, String>> = emptyList()): String {
        val expenseCategories = (builtinExpenseCategories + customCategories.filter { (id, _) ->
            builtinExpenseCategories.none { it.first == id } && builtinIncomeCategories.none { it.first == id }
        }).distinctBy { it.first }

        val incomeCategories = builtinIncomeCategories

        val expenseList = expenseCategories.joinToString(", ") { (id, name) -> "$id=$name" }
        val incomeList = incomeCategories.joinToString(", ") { (id, name) -> "$id=$name" }

        val today = java.time.LocalDate.now()
        val dayOfWeek = when (today.dayOfWeek.value) {
            1 -> "一"; 2 -> "二"; 3 -> "三"; 4 -> "四"; 5 -> "五"; 6 -> "六"; 7 -> "日"; else -> ""
        }

        return """你是一个专业的个人财务记账助手。用户会发送消费描述或账单图片，你需要准确识别其中的账单信息并返回JSON。

## 当前时间
今天是${today}（星期${dayOfWeek}），请据此换算相对日期。

## 输出格式
只返回一个JSON对象，不要包含任何其他文字、解释或markdown标记。不要用```json```包裹。
如果无法识别出账单信息，返回 {"error":"原因"}

## JSON字段
- type: "expense"(支出) 或 "income"(收入)
- title: 简洁标题，2-6个字，如"午餐"、"打车"、"超市购物"
- category: 必须从以下分类ID中选择最匹配的一个
- amount: 金额数字（单位：元），必须大于0，如 35.5
- note: 补充说明（如具体商品名、地点），无则填 ""
- date: 消费日期，格式 YYYY-MM-DD。如果图片中日期和时间连在一起（如"2025-05-24 19:36:02"），date只填日期部分"2025-05-24"，时间部分填入time字段
- time: 消费时间，格式 HH:MM:SS。图片中的时间必须提取！如"2025-05-24 19:36:02"→time填"19:36:02"；"14:30"→"14:30:00"；"19:36:02"→"19:36:02"。文字描述中仅当用户明确提到具体时间点时才填写

## 分类选项（ID=中文名）
支出：$expenseList
收入：$incomeList

## 规则
1. 退款/退货记录type设为"income"，title写"退款-原事项"，如"退款-外卖"
2. 折扣、优惠、满减不影响实际支付金额，amount填实际支付金额
3. amount必须大于0，免费或0元返回 {"error":"无法识别"}
4. 预付/分期金额填总额，如"预付3个月房租9000"则amount填9000
5. 如果有多笔账单，只返回金额最大的一笔
6. category必须从给定分类ID中选择，选最匹配的
7. title应具体化，如"星巴克"而非"餐饮"，"滴滴"而非"交通"
8. 金额为整数时不要加小数点，如35而非35.0
9. "昨天"换算为${today.minusDays(1)}，"前天"换算为${today.minusDays(2)}，以此类推
10. time字段：图片中显示的时间必须提取！即使日期和时间连在一起（如"2025-05-24 19:36:02"），也要分别填入date和time字段，time必须精确到秒。文字描述中仅填写用户明确提到的具体时间点，时间段词不填
11. 图片中的日期时间格式多种多样，常见格式及正确拆分方式：
   - "2025-05-24 19:36:02" → date="2025-05-24", time="19:36:02"
   - "2025/05/24 19:36" → date="2025-05-24", time="19:36:00"
   - "2025-05-24T19:36" → date="2025-05-24", time="19:36:00"
   - "24/05/2025 19:36" → date="2025-05-24", time="19:36:00"
   注意：绝不能将时间部分丢弃！如果图片中有时间信息，time字段必须填写，且必须包含秒数

## 示例
"午餐花了35元" → {"type":"expense","title":"午餐","category":"food","amount":35,"note":"","date":"","time":""}
"昨天打车28.5" → {"type":"expense","title":"打车","category":"transport","amount":28.5,"note":"","date":"${today.minusDays(1)}","time":""}
"今天早上喝胡辣汤花了8块钱" → {"type":"expense","title":"胡辣汤","category":"food","amount":8,"note":"","date":"$today","time":""}
"昨天晚上吃饭花了25" → {"type":"expense","title":"吃饭","category":"food","amount":25,"note":"","date":"${today.minusDays(1)}","time":""}
"下午3点喝咖啡32" → {"type":"expense","title":"咖啡","category":"food","amount":32,"note":"","date":"","time":"15:00:00"}
"外卖退款15元" → {"type":"income","title":"退款-外卖","category":"food","amount":15,"note":"","date":"","time":""}
"工资到账8000" → {"type":"income","title":"工资","category":"salary","amount":8000,"note":"","date":"","time":""}
"给猫买粮120" → {"type":"expense","title":"猫粮","category":"pet","amount":120,"note":"","date":"","time":""}
"永辉超市买了156.8的东西" → {"type":"expense","title":"超市购物","category":"shopping","amount":156.8,"note":"永辉超市","date":"","time":""}
"手机充话费50" → {"type":"expense","title":"话费","category":"digital","amount":50,"note":"","date":"","time":""}
"看牙花了380" → {"type":"expense","title":"看牙","category":"health","amount":380,"note":"","date":"","time":""}
[图片识别]小票显示14:30消费35元 → {"type":"expense","title":"消费","category":"daily","amount":35,"note":"","date":"","time":"14:30:00"}
[图片识别]小票显示 2025-05-24 19:36:02 消费35元 → {"type":"expense","title":"消费","category":"daily","amount":35,"note":"","date":"2025-05-24","time":"19:36:02"}"""
    }

    suspend fun sendChatMessage(
        apiAddress: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        maxTokens: Int = 1024,
        maxRetries: Int = 3,
        customCategories: List<Pair<String, String>> = emptyList(),
        onRetry: ((Int) -> Unit)? = null
    ): Result<AiResponse> = executeWithRetry(maxRetries, onRetry, customCategories.map { it.first }) {
        val systemPrompt = buildSystemPrompt(customCategories)
        val requestBody = buildJsonObject {
            put("model", model)
            put("messages", buildJsonArray {
                add(buildJsonObject {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                messages.forEach { msg ->
                    add(buildJsonObject {
                        put("role", msg.role)
                        put("content", msg.content)
                    })
                }
            })
            put("max_tokens", maxTokens)
            put("temperature", 0.1)
        }.toString().toRequestBody("application/json".toMediaType())

        Request.Builder()
            .url(apiAddress)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
    }

    suspend fun sendVisionMessage(
        apiAddress: String,
        apiKey: String,
        model: String,
        text: String,
        imageBase64: String,
        chatHistory: List<ChatMessage> = emptyList(),
        maxTokens: Int = 1024,
        maxRetries: Int = 3,
        customCategories: List<Pair<String, String>> = emptyList(),
        onRetry: ((Int) -> Unit)? = null
    ): Result<AiResponse> = executeWithRetry(maxRetries, onRetry, customCategories.map { it.first }) {
        val systemPrompt = buildSystemPrompt(customCategories)
        val userText = text.ifBlank { "请识别这张图片中的账单信息" }
        val messagesPayload = buildJsonArray {
            add(buildJsonObject {
                put("role", "system")
                put("content", systemPrompt)
            })
            chatHistory.forEach { msg ->
                add(buildJsonObject {
                    put("role", msg.role)
                    put("content", msg.content)
                })
            }
            add(buildJsonObject {
                put("role", "user")
                put("content", buildJsonArray {
                    add(buildJsonObject {
                        put("type", "text")
                        put("text", userText)
                    })
                    add(buildJsonObject {
                        put("type", "image_url")
                        put("image_url", buildJsonObject {
                            put("url", "data:image/jpeg;base64,$imageBase64")
                        })
                    })
                })
            })
        }

        val requestBody = buildJsonObject {
            put("model", model)
            put("messages", messagesPayload)
            put("max_tokens", maxTokens)
            put("temperature", 0.1)
        }.toString().toRequestBody("application/json".toMediaType())

        Request.Builder()
            .url(apiAddress)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
    }

    private suspend fun executeWithRetry(
        maxRetries: Int,
        onRetry: ((Int) -> Unit)?,
        customCategoryIds: List<String> = emptyList(),
        buildRequest: () -> Request
    ): Result<AiResponse> = withContext(Dispatchers.IO) {
        var lastException: Exception? = null
        repeat(maxRetries) { attempt ->
            try {
                if (attempt > 0) {
                    onRetry?.invoke(attempt)
                    val baseDelay = (1L shl attempt) * 1000L
                    val jitter = Random.nextLong(0, 500)
                    delay(baseDelay + jitter)
                }

                val request = buildRequest()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: return@withContext Result.failure(
                    Exception("Empty response body")
                )

                if (!response.isSuccessful) {
                    lastException = Exception("API error: ${response.code}")
                    if (attempt < maxRetries - 1) return@repeat
                    return@withContext Result.failure(lastException!!)
                }

                val content = parseContentFromResponse(responseBody)
                    ?: return@withContext Result.failure(Exception("No content in response"))

                val parsedBill = parseBillFromContent(content, customCategoryIds)
                if (parsedBill != null) {
                    return@withContext Result.success(AiResponse(content = content, parsedBill = parsedBill))
                }

                lastException = Exception("无法解析AI返回的账单信息")
                if (attempt < maxRetries - 1) return@repeat
                return@withContext Result.failure(lastException!!)
            } catch (e: IOException) {
                lastException = e
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxRetries - 1) return@repeat
                return@withContext Result.failure(e)
            }
        }
        Result.failure(lastException ?: Exception("Unknown error"))
    }

    private fun parseContentFromResponse(responseBody: String): String? {
        return try {
            val jsonResponse = json.parseToJsonElement(responseBody).jsonObject
            val content = jsonResponse["choices"]?.jsonArray
                ?.firstOrNull()?.jsonObject
                ?.get("message")?.jsonObject
                ?.get("content")?.jsonPrimitive?.content
            Log.d("AiApiService", "parseContentFromResponse: content='$content'")
            content
        } catch (e: Exception) {
            Log.e("AiApiService", "parseContentFromResponse failed: ${e.message}")
            null
        }
    }

    private fun parseBillFromContent(content: String, customCategoryIds: List<String> = emptyList()): ParsedBill? {
        val cleaned = content.trim()
        val jsonStr = extractJsonString(cleaned)
        if (jsonStr == null) {
            Log.w("AiApiService", "extractJsonString returned null for content: ${cleaned.take(200)}")
            return null
        }

        return try {
            val billJson = json.parseToJsonElement(jsonStr).jsonObject

            if (billJson.containsKey("error")) {
                Log.w("AiApiService", "AI returned error: ${billJson["error"]}")
                return null
            }

            val type = billJson["type"]?.jsonPrimitive?.content ?: "expense"
            val title = billJson["title"]?.jsonPrimitive?.content ?: return null
            val category = billJson["category"]?.jsonPrimitive?.content ?: "daily"
            val amount = parseAmount(billJson["amount"]) ?: return null
            if (amount <= 0) return null
            val note = billJson["note"]?.let { el ->
                if (el is kotlinx.serialization.json.JsonPrimitive) el.content else ""
            } ?: ""
            val date = billJson["date"]?.let { el ->
                if (el is kotlinx.serialization.json.JsonPrimitive) el.content.trim() else ""
            } ?: ""
            val time = billJson["time"]?.let { el ->
                if (el is kotlinx.serialization.json.JsonPrimitive) el.content.trim() else ""
            } ?: ""

            Log.d("AiApiService", "parseBillFromContent: type=$type, title=$title, category=$category, amount=$amount, date='$date', time='$time', rawJson=${billJson.toString().take(300)}")

            val validCategories = (builtinExpenseCategories.map { it.first } + builtinIncomeCategories.map { it.first } + customCategoryIds).toSet()

            ParsedBill(
                type = if (type == "income") "income" else "expense",
                title = title,
                category = if (category in validCategories) category else "daily",
                amount = amount,
                note = note,
                date = date,
                time = time
            )
        } catch (e: Exception) {
            Log.w("AiApiService", "parseBillFromContent failed: ${e.message}, jsonStr: ${jsonStr.take(200)}")
            null
        }
    }

    private fun extractJsonString(text: String): String? {
        var cleaned = text.trim()

        val codeBlockStart = cleaned.indexOf("```")
        if (codeBlockStart != -1) {
            val afterStart = codeBlockStart + 3
            val langEnd = cleaned.indexOf('\n', afterStart)
            val jsonStart = if (langEnd != -1 && langEnd < cleaned.indexOf('{', afterStart)) langEnd + 1 else afterStart
            val codeBlockEnd = cleaned.lastIndexOf("```")
            if (codeBlockEnd > codeBlockStart) {
                cleaned = cleaned.substring(jsonStart, codeBlockEnd).trim()
            }
        }

        val firstBrace = cleaned.indexOf('{')
        if (firstBrace == -1) return null

        var depth = 0
        var inString = false
        var escape = false
        var lastBrace = -1

        for (i in firstBrace until cleaned.length) {
            val c = cleaned[i]
            if (escape) {
                escape = false
                continue
            }
            if (c == '\\' && inString) {
                escape = true
                continue
            }
            if (c == '"') {
                inString = !inString
                continue
            }
            if (inString) continue

            when (c) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) {
                        lastBrace = i
                        break
                    }
                }
            }
        }

        if (lastBrace == -1) return null
        return cleaned.substring(firstBrace, lastBrace + 1)
    }

    private fun parseAmount(element: JsonElement?): Long? {
        if (element == null) return null
        return try {
            val prim = element.jsonPrimitive
            val yuan = prim.doubleOrNull ?: prim.content.toDoubleOrNull() ?: prim.int.toDouble()
            Math.round(yuan * 100)
        } catch (_: Exception) {
            null
        }
    }
}

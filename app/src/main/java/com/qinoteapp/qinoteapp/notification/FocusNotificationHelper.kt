package com.qinoteapp.qinoteapp.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.qinoteapp.qinoteapp.MainActivity
import com.xzakota.hyper.notification.focus.FocusNotification
import com.xzakota.hyper.notification.focus.model.ActionInfo
import com.xzakota.hyper.notification.focus.template.FocusTemplateV3
import com.xzakota.hyper.notification.island.model.TextInfo

class FocusNotificationHelper(private val context: Context) {

    companion object {
        private const val COLOR_EXPENSE = "#EF4444"
        private const val COLOR_INCOME = "#22C55E"
        private const val COLOR_PARSING = "#3B82F6"
        private const val COLOR_FAILED = "#EF4444"
        private const val BUSINESS_BOOKKEEPING = "bookkeeping"
    }

    fun buildNotification(data: BookkeepingNotificationData): Bundle {
        val contentIntentUri = buildContentIntentUri(data)

        return FocusNotification.buildV3 {
            enableFloat = true
            islandFirstFloat = true
            updatable = true
            business = BUSINESS_BOOKKEEPING

            when (data.state) {
                BookkeepingState.PARSING -> buildParsingState(this, data)
                BookkeepingState.SUCCESS -> buildSuccessState(this, data, contentIntentUri)
                BookkeepingState.FAILED -> buildFailedState(this, data, contentIntentUri)
            }
        }
    }

    private fun buildContentIntentUri(data: BookkeepingNotificationData): String {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_home", true)
        }
        return intent.toUri(Intent.URI_INTENT_SCHEME)
    }

    private fun buildParsingState(template: FocusTemplateV3, data: BookkeepingNotificationData) {
        template.hintInfo {
            type = 1
            title = "解析中"
            content = "AI分析中"
        }

        template.chatInfo {
            title = "正在解析…"
            content = "AI正在分析您的输入"
        }

        template.picInfo {
            type = 0
        }

        template.island {
            islandProperty = 1
            islandTimeout = 300
            highlightColor = COLOR_PARSING
            bigIslandArea {
                imageTextInfoLeft {
                    type = 1
                    textInfo {
                        frontTitle = "\u23F3"
                        title = "解析中"
                        showHighlightColor = true
                    }
                }
                textInfo = TextInfo().apply {
                    title = "AI分析中"
                }
            }
            smallIslandArea {
                picInfo {
                    type = 0
                }
            }
        }
    }

    private fun buildSuccessState(
        template: FocusTemplateV3,
        data: BookkeepingNotificationData,
        contentIntentUri: String
    ) {
        val isIncome = data.type == "income"
        val colorStr = if (isIncome) COLOR_INCOME else COLOR_EXPENSE
        val sign = if (isIncome) "+" else "-"
        val typeLabel = if (isIncome) "收入" else "支出"
        val displayAmount = "$sign${data.amount}"

        template.hintInfo {
            type = 1
            title = displayAmount
            colorTitle = colorStr
            content = "已记录"
        }

        template.chatInfo {
            title = data.title
            content = "$typeLabel · ${data.categoryName}"
        }

        template.picInfo {
            type = 0
        }

        template.actions {
            add(ActionInfo().apply {
                type = 2
                actionTitle = displayAmount
                actionTitleColor = colorStr
                clickWithCollapse = true
            })
            add(ActionInfo().apply {
                type = 2
                actionTitle = "查看"
                actionIntent = contentIntentUri
                actionIntentType = 1
                clickWithCollapse = true
            })
        }

        template.island {
            islandProperty = 1
            islandTimeout = 30
            highlightColor = colorStr
            bigIslandArea {
                imageTextInfoLeft {
                    type = 1
                    textInfo {
                        frontTitle = if (isIncome) "\uD83D\uDCB0" else "\uD83D\uDCB8"
                        title = typeLabel
                        showHighlightColor = true
                    }
                }
                textInfo = TextInfo().apply {
                    title = displayAmount
                    showHighlightColor = true
                }
            }
            smallIslandArea {
                picInfo {
                    type = 0
                }
            }
        }
    }

    private fun buildFailedState(
        template: FocusTemplateV3,
        data: BookkeepingNotificationData,
        contentIntentUri: String
    ) {
        val errorSummary = data.errorMessage?.take(10) ?: "未知错误"

        template.hintInfo {
            type = 1
            title = "失败"
            content = errorSummary
        }

        template.chatInfo {
            title = "记账失败"
            content = data.errorMessage ?: "请重试"
        }

        template.picInfo {
            type = 0
        }

        template.actions {
            add(ActionInfo().apply {
                type = 2
                actionTitle = "重试"
                actionIntent = contentIntentUri
                actionIntentType = 1
                clickWithCollapse = true
            })
        }

        template.island {
            islandProperty = 1
            islandTimeout = 60
            highlightColor = COLOR_FAILED
            bigIslandArea {
                imageTextInfoLeft {
                    type = 1
                    textInfo {
                        frontTitle = "\u274C"
                        title = "解析失败"
                        showHighlightColor = true
                    }
                }
                textInfo = TextInfo().apply {
                    title = errorSummary
                }
            }
            smallIslandArea {
                picInfo {
                    type = 0
                }
            }
        }
    }
}

package com.qinoteapp.qinoteapp.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.qinoteapp.qinoteapp.R

class LiveUpdateNotificationHelper(private val context: Context) {

    fun buildNotification(
        data: BookkeepingNotificationData,
        channelId: String,
        pendingIntent: PendingIntent,
        retryIntent: PendingIntent?
    ): Notification {
        return if (Build.VERSION.SDK_INT >= 36) {
            buildProgressStyleNotification(data, channelId, pendingIntent, retryIntent)
        } else {
            buildOngoingNotification(data, channelId, pendingIntent, retryIntent)
        }
    }

    private fun buildProgressStyleNotification(
        data: BookkeepingNotificationData,
        channelId: String,
        pendingIntent: PendingIntent,
        retryIntent: PendingIntent?
    ): Notification {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSubText("奇记")

        when (data.state) {
            BookkeepingState.PARSING -> {
                builder.setContentTitle("AI记账解析中")
                    .setContentText(if (data.title.isNotBlank()) data.title else "正在分析…")
                    .setProgress(100, 0, true)
            }
            BookkeepingState.SUCCESS -> {
                val isIncome = data.type == "income"
                val typeLabel = if (isIncome) "收入" else "支出"
                val amountDisplay = if (isIncome) "+${data.amount}" else "-${data.amount}"

                builder.setContentTitle("记账成功 · $amountDisplay")
                    .setContentText("$typeLabel · ${data.categoryName} · ${data.title}")
                    .setProgress(0, 0, false)
            }
            BookkeepingState.FAILED -> {
                builder.setContentTitle("记账失败")
                    .setContentText(data.errorMessage ?: "请重试")
                    .setProgress(0, 0, false)
                retryIntent?.let {
                    builder.addAction(R.drawable.ic_notification, "重试", it)
                }
            }
        }

        return builder.build()
    }

    private fun buildOngoingNotification(
        data: BookkeepingNotificationData,
        channelId: String,
        pendingIntent: PendingIntent,
        retryIntent: PendingIntent?
    ): Notification {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSubText("奇记")

        when (data.state) {
            BookkeepingState.PARSING -> {
                builder.setContentTitle("AI记账解析中…")
                    .setContentText(if (data.title.isNotBlank()) data.title else "正在分析…")
                    .setProgress(100, 0, true)
            }
            BookkeepingState.SUCCESS -> {
                val isIncome = data.type == "income"
                val typeLabel = if (isIncome) "收入" else "支出"
                val amountDisplay = if (isIncome) "+${data.amount}" else "-${data.amount}"

                builder.setContentTitle("记账成功 · $amountDisplay")
                    .setContentText("$typeLabel · ${data.categoryName} · ${data.title}")
                    .setProgress(0, 0, false)
            }
            BookkeepingState.FAILED -> {
                builder.setContentTitle("记账失败")
                    .setContentText(data.errorMessage ?: "请重试")
                    .setProgress(0, 0, false)
                retryIntent?.let {
                    builder.addAction(R.drawable.ic_notification, "重试", it)
                }
            }
        }

        return builder.build()
    }
}

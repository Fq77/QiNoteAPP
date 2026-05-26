package com.qinoteapp.qinoteapp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.qinoteapp.qinoteapp.MainActivity
import com.qinoteapp.qinoteapp.R
import com.qinoteapp.qinoteapp.notification.shizuku.ShizukuWhitelistBypass
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookkeepingNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "BookkeepingNotify"
        private const val CHANNEL_ID = "bookkeeping_channel"
        private const val CHANNEL_NAME = "AI记账通知"
        private const val NOTIFICATION_ID = 20001
        private const val AUTO_CANCEL_DELAY_MS = 30_000L
        const val ACTION_BOOKKEEPING_RETRY = "com.qinoteapp.qinoteapp.ACTION_BOOKKEEPING_RETRY"
        const val EXTRA_NOTIFICATION_STATE = "notification_state"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val handler = Handler(Looper.getMainLooper())

    private val capabilityDetector = DeviceCapabilityDetector(context)
    private val strategyResolver = NotificationStrategyResolver(capabilityDetector)
    private val focusHelper = FocusNotificationHelper(context)
    private val liveUpdateHelper = LiveUpdateNotificationHelper(context)

    private var currentStrategy: NotificationStrategy = strategyResolver.resolve()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "AI记账实时状态通知"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(data: BookkeepingNotificationData) {
        currentStrategy = strategyResolver.resolve()
        Log.d(TAG, "Showing notification with strategy: $currentStrategy, state: ${data.state}")

        when (currentStrategy) {
            NotificationStrategy.SUPER_ISLAND_DIRECT -> showSuperIslandNotification(data)
            NotificationStrategy.SUPER_ISLAND_SHIZUKU -> showSuperIslandShizukuNotification(data)
            NotificationStrategy.LIVE_UPDATES -> showLiveUpdateNotification(data)
            NotificationStrategy.ONGOING_NOTIFICATION -> showLiveUpdateNotification(data)
        }

        if (data.state == BookkeepingState.SUCCESS) {
            scheduleAutoCancel()
        }
    }

    fun updateNotification(data: BookkeepingNotificationData) {
        showNotification(data)
    }

    fun cancelNotification() {
        handler.removeCallbacksAndMessages(null)
        try {
            notificationManager.cancel(NOTIFICATION_ID)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to cancel notification: ${e.message}")
        }
    }

    private fun showSuperIslandNotification(data: BookkeepingNotificationData) {
        val extras = focusHelper.buildNotification(data)
        val notification = buildBaseNotification(data, extras)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showSuperIslandShizukuNotification(data: BookkeepingNotificationData) {
        val extras = focusHelper.buildNotification(data)
        val notification = buildBaseNotification(data, extras)

        val sent = ShizukuWhitelistBypass.notifyAsShell(
            context, NOTIFICATION_ID, CHANNEL_ID, notification
        )

        if (!sent) {
            Log.w(TAG, "Shizuku bypass failed, falling back to live update")
            showLiveUpdateNotification(data)
        }
    }

    private fun showLiveUpdateNotification(data: BookkeepingNotificationData) {
        val pendingIntent = createContentPendingIntent(data)
        val retryIntent = createRetryPendingIntent(data)
        val notification = liveUpdateHelper.buildNotification(data, CHANNEL_ID, pendingIntent, retryIntent)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildBaseNotification(data: BookkeepingNotificationData, focusExtras: android.os.Bundle): Notification {
        val pendingIntent = createContentPendingIntent(data)
        val retryIntent = createRetryPendingIntent(data)

        val typeLabel = if (data.type == "income") "收入" else "支出"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSubText("奇记")
            .addExtras(focusExtras)

        when (data.state) {
            BookkeepingState.PARSING -> {
                builder.setContentTitle("AI记账解析中")
                    .setContentText("正在分析…")
            }
            BookkeepingState.SUCCESS -> {
                val amountDisplay = if (data.type == "income") "+${data.amount}" else "-${data.amount}"
                val titleText = "记账成功 · $amountDisplay"
                val contentText = "$typeLabel · ${data.categoryName} · ${data.title}"

                builder.setContentTitle(titleText)
                    .setContentText(contentText)
            }
            BookkeepingState.FAILED -> {
                builder.setContentTitle("记账失败")
                    .setContentText(data.errorMessage ?: "请重试")
            }
        }

        return builder.build()
    }

    private fun createContentPendingIntent(data: BookkeepingNotificationData): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            when (data.state) {
                BookkeepingState.PARSING, BookkeepingState.FAILED -> {
                    putExtra("open_entry", true)
                }
                BookkeepingState.SUCCESS -> {
                    putExtra("open_home", true)
                }
            }
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createRetryPendingIntent(data: BookkeepingNotificationData): PendingIntent? {
        if (data.state != BookkeepingState.FAILED) return null

        val intent = Intent(ACTION_BOOKKEEPING_RETRY).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_NOTIFICATION_STATE, data.state.name)
        }
        return PendingIntent.getBroadcast(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleAutoCancel() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            cancelNotification()
        }, AUTO_CANCEL_DELAY_MS)
    }

    fun refreshStrategy() {
        strategyResolver.invalidateCache()
        currentStrategy = strategyResolver.resolve()
    }
}

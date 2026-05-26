package com.qinoteapp.qinoteapp.notification.shizuku

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import rikka.shizuku.Shizuku

object ShizukuWhitelistBypass {
    private const val TAG = "ShizukuBypass"

    var onServiceDisconnected: (() -> Unit)? = null

    private val listener = Shizuku.OnBinderReceivedListener {}
    private val deadListener = Shizuku.OnBinderDeadListener {
        Log.w(TAG, "Shizuku service died")
        onServiceDisconnected?.invoke()
    }

    fun init() {
        Shizuku.addBinderReceivedListener(listener)
        Shizuku.addBinderDeadListener(deadListener)
    }

    fun destroy() {
        Shizuku.removeBinderReceivedListener(listener)
        Shizuku.removeBinderDeadListener(deadListener)
    }

    fun isShizukuReady(): Boolean {
        return try {
            Shizuku.pingBinder() && Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    fun notifyAsShell(
        context: Context,
        notificationId: Int,
        channelId: String,
        notification: Notification
    ): Boolean {
        return try {
            if (!isShizukuReady()) {
                Log.w(TAG, "Shizuku not ready")
                return false
            }
            val shellContext = ShizukuContext(context)
            val nm = shellContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, notification)
            Log.d(TAG, "Notification sent as shell successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send notification as shell: ${e.message}")
            false
        }
    }
}

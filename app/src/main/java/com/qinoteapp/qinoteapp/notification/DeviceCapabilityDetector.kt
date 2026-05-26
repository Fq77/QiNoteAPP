package com.qinoteapp.qinoteapp.notification

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import rikka.shizuku.Shizuku

class DeviceCapabilityDetector(private val context: Context) {

    companion object {
        private const val TAG = "DeviceCapability"
    }

    fun isXiaomiDevice(): Boolean {
        return Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true) ||
               Build.MANUFACTURER.equals("Redmi", ignoreCase = true)
    }

    fun getFocusProtocolVersion(): Int {
        return try {
            android.provider.Settings.System.getInt(
                context.contentResolver,
                "notification_focus_protocol",
                0
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get focus protocol version: ${e.message}")
            0
        }
    }

    fun isHyperOS3Plus(): Boolean {
        return isXiaomiDevice() && getFocusProtocolVersion() >= 3
    }

    fun hasFocusPermission(): Boolean {
        return try {
            val uri = Uri.parse("content://miui.statusbar.notification.public")
            val extras = Bundle().apply {
                putString("package", context.packageName)
            }
            val bundle = context.contentResolver.call(uri, "canShowFocus", null, extras)
            bundle?.getBoolean("canShowFocus", false) ?: false
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check focus permission: ${e.message}")
            false
        }
    }

    fun isIslandSupported(): Boolean {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getDeclaredMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(null, "persist.sys.feature.island", false) as? Boolean ?: false
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check island support: ${e.message}")
            false
        }
    }

    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    fun isShizukuPermissionGranted(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    fun isShizukuReady(): Boolean {
        return isShizukuAvailable() && isShizukuPermissionGranted()
    }

    fun isAndroid16Plus(): Boolean {
        return Build.VERSION.SDK_INT >= 36
    }
}

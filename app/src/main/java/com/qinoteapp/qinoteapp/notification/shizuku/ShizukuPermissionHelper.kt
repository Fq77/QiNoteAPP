package com.qinoteapp.qinoteapp.notification.shizuku

import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import rikka.shizuku.Shizuku

object ShizukuPermissionHelper {
    private const val REQUEST_CODE = 1001
    private const val TAG = "ShizukuPermission"

    suspend fun requestPermission(): Boolean {
        return try {
            if (!Shizuku.pingBinder()) return false
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) return true

            callbackFlow {
                val listener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
                    trySend(grantResult == PackageManager.PERMISSION_GRANTED)
                }
                Shizuku.addRequestPermissionResultListener(listener)
                Shizuku.requestPermission(REQUEST_CODE)
                awaitClose { Shizuku.removeRequestPermissionResultListener(listener) }
            }.first()
        } catch (e: Exception) {
            Log.e(TAG, "Shizuku permission request failed: ${e.message}")
            false
        }
    }
}

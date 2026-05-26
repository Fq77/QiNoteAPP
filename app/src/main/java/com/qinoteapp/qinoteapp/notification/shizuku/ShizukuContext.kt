package com.qinoteapp.qinoteapp.notification.shizuku

import android.content.AttributionSource
import android.content.ContextWrapper
import android.os.Build
import rikka.shizuku.Shizuku

class ShizukuContext(base: android.content.Context) : ContextWrapper(base) {
    override fun getOpPackageName(): String = "com.android.shell"

    override fun getAttributionSource(): AttributionSource {
        val shellUid = try {
            Shizuku.getUid()
        } catch (_: Throwable) {
            2000
        }
        val builder = AttributionSource.Builder(shellUid)
            .setPackageName("com.android.shell")
        if (Build.VERSION.SDK_INT >= 34) {
            builder.setPid(-1)
        }
        return builder.build()
    }
}

package com.qinoteapp.qinoteapp

import android.app.Application
import com.qinoteapp.qinoteapp.data.local.PreferencesManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class QiNoteApp : Application() {
    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            preferencesManager.migrateLegacyApiKey()
        }
    }
}

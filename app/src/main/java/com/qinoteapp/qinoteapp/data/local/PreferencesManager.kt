package com.qinoteapp.qinoteapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class AiConfig(
    val aiEnabled: Boolean = true,
    val apiAddress: String = "https://open.bigmodel.cn/api/paas/v4/chat/completions",
    val apiKey: String = "",
    val textModel: String = "glm-4.6v-flash",
    val visionModel: String = "glm-4.6v-flash",
    val autoRetry: Boolean = true,
    val saveChatHistory: Boolean = false
)

data class AppConfig(
    val themeMode: String = "system",
    val predictiveBack: Boolean = true
)

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val AI_ENABLED = booleanPreferencesKey("ai_enabled")
        val API_ADDRESS = stringPreferencesKey("api_address")
        val API_KEY = stringPreferencesKey("api_key")
        val TEXT_MODEL = stringPreferencesKey("text_model")
        val VISION_MODEL = stringPreferencesKey("vision_model")
        val AUTO_RETRY = booleanPreferencesKey("auto_retry")
        val SAVE_CHAT_HISTORY = booleanPreferencesKey("save_chat_history")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PREDICTIVE_BACK = booleanPreferencesKey("predictive_back")
        val SUPER_ISLAND_ENABLED = booleanPreferencesKey("super_island_enabled")
    }

    val aiConfig: Flow<AiConfig> = dataStore.data.map { prefs ->
        AiConfig(
            aiEnabled = prefs[Keys.AI_ENABLED] ?: true,
            apiAddress = prefs[Keys.API_ADDRESS] ?: "https://open.bigmodel.cn/api/paas/v4/chat/completions",
            apiKey = prefs[Keys.API_KEY] ?: "",
            textModel = prefs[Keys.TEXT_MODEL] ?: "glm-4.6v-flash",
            visionModel = prefs[Keys.VISION_MODEL] ?: "glm-4.6v-flash",
            autoRetry = prefs[Keys.AUTO_RETRY] ?: true,
            saveChatHistory = prefs[Keys.SAVE_CHAT_HISTORY] ?: false
        )
    }

    val superIslandEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SUPER_ISLAND_ENABLED] ?: true
    }

    val appConfig: Flow<AppConfig> = dataStore.data.map { prefs ->
        AppConfig(
            themeMode = prefs[Keys.THEME_MODE] ?: "system",
            predictiveBack = prefs[Keys.PREDICTIVE_BACK] ?: true
        )
    }

    suspend fun updateAiConfig(config: AiConfig) {
        dataStore.edit { prefs ->
            prefs[Keys.AI_ENABLED] = config.aiEnabled
            prefs[Keys.API_ADDRESS] = config.apiAddress
            prefs[Keys.API_KEY] = config.apiKey
            prefs[Keys.TEXT_MODEL] = config.textModel
            prefs[Keys.VISION_MODEL] = config.visionModel
            prefs[Keys.AUTO_RETRY] = config.autoRetry
            prefs[Keys.SAVE_CHAT_HISTORY] = config.saveChatHistory
        }
    }

    suspend fun setSuperIslandEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SUPER_ISLAND_ENABLED] = enabled
        }
    }

    suspend fun updateAppConfig(config: AppConfig) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = config.themeMode
            prefs[Keys.PREDICTIVE_BACK] = config.predictiveBack
        }
    }
}

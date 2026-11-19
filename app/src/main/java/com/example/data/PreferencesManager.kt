package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.studioDataStore by preferencesDataStore(name = "studio_pixy_prefs")

class PreferencesManager(private val context: Context) {

    private val appContext = context.applicationContext

    private object Keys {
        val HINDI_FRIENDLY = booleanPreferencesKey("hindi_friendly_mode")
        val ADMIN_PASSCODE = stringPreferencesKey("admin_passcode")
    }

    val hindiFriendlyMode: Flow<Boolean> = appContext.studioDataStore.data
        .map { prefs -> prefs[Keys.HINDI_FRIENDLY] ?: true }

    val adminPasscode: Flow<String> = appContext.studioDataStore.data
        .map { prefs -> prefs[Keys.ADMIN_PASSCODE] ?: DefaultStudioSeed.DEFAULT_ADMIN_PASSCODE }

    suspend fun setHindiFriendlyMode(enabled: Boolean) {
        appContext.studioDataStore.edit { prefs ->
            prefs[Keys.HINDI_FRIENDLY] = enabled
        }
    }

    suspend fun setAdminPasscode(newPasscode: String) {
        val sanitized = newPasscode.trim()
        if (sanitized.length >= 4) {
            appContext.studioDataStore.edit { prefs ->
                prefs[Keys.ADMIN_PASSCODE] = sanitized
            }
        }
    }
}

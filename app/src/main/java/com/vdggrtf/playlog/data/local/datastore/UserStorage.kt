package com.vdggrtf.playlog.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStorage: DataStore<Preferences> by preferencesDataStore(name = "playlog_prefs")

@Singleton
class UserStorage @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val LAST_SCREEN = stringPreferencesKey("last_screen")
        val USER_TOKEN = stringPreferencesKey("user_token")

        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val userEmail: Flow<String> = context.dataStorage.data.map { it[USER_EMAIL] ?: "" }

    val userName: Flow<String> = context.dataStorage.data.map { it[USER_NAME] ?: "Gamer" }

    val lastScreen: Flow<String?> = context.dataStorage.data.map { it[LAST_SCREEN] }

    val userToken: Flow<String?> = context.dataStorage.data.map { it[USER_TOKEN] }

    val refreshToken: Flow<String?> = context.dataStorage.data.map { it[REFRESH_TOKEN] }

    suspend fun saveToken(accessToken: String, refreshToken: String) {
        context.dataStorage.edit {
            it[USER_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUserData(name: String, email: String) {
        context.dataStorage.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun saveLastScreen(route: String) {
        context.dataStorage.edit { it[LAST_SCREEN] = route }
    }

    suspend fun clearStorage() {
        context.dataStorage.edit { it.clear() }
    }
}
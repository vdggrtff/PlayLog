package com.vdggrtf.playlog.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.data.local.datastore.UserStorage
import com.vdggrtf.playlog.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val userStorage: UserStorage,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            delay(500)

            try {
                // Retrieving the token from DataStore
                val savedAccessToken = userStorage.userToken.first()
                val savedRefreshToken = userStorage.refreshToken.first()

                if (!savedAccessToken.isNullOrBlank()) {
                    try {
                        supabase.auth.importAuthToken(
                            accessToken = savedAccessToken,
                            refreshToken = savedRefreshToken ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("SplashVM", "Supabase не принял токен: ${e.message}")
                    }

                    _startDestination.value = Screen.LibraryScreen.route
                } else {
                    _startDestination.value = Screen.LoginScreen.route
                }
            } catch (e: Exception) {
                // if there is no internet but a token was present - still allow offline mode!
                val savedToken = userStorage.userToken.first()
                if (savedToken != null) {
                    _startDestination.value = Screen.LibraryScreen.route
                } else {
                    _startDestination.value = Screen.LoginScreen.route
                }
            }
        }
    }
}
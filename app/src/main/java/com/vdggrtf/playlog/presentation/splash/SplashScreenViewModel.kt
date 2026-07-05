package com.vdggrtf.playlog.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val supabase: SupabaseClient,
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
                // 1. Ждем, пока Supabase сам найдет токены в памяти телефона
                // и обновит их на сервере (если они протухли)
                supabase.auth.awaitInitialization()

                // 2. Спрашиваем: "Ну что, есть живая сессия?"
                val session = supabase.auth.currentSessionOrNull()

                if (session != null) {
                    Log.d("SplashVM", "Автопилот сработал! Токен жив. Идем в Библиотеку.")
                    _startDestination.value = Screen.LibraryScreen.route
                } else {
                    Log.d("SplashVM", "Сессии нет. Идем логиниться.")
                    _startDestination.value = Screen.LoginScreen.route
                }
            } catch (e: Exception) {
                Log.e("SplashVM", "Ошибка инициализации Supabase: ${e.message}")
                // Если нет интернета, SDK кинет ошибку при попытке рефреша.
                // Включаем OFFLINE-FIRST логику:
                // Раз SDK упал без сети, но мы знаем, что раньше юзер заходил (токен где-то есть)
                val currentSession = supabase.auth.currentSessionOrNull()
                if (currentSession != null) {
                    _startDestination.value = Screen.LibraryScreen.route
                } else {
                    _startDestination.value = Screen.LoginScreen.route
                }
            }
        }
    }
}
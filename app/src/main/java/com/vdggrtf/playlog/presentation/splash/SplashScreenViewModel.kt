package com.vdggrtf.playlog.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.usecase.splash.CheckUserSessionUseCase
import com.vdggrtf.playlog.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val checkUserSessionUseCase: CheckUserSessionUseCase,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            delay(500)

            val isUserLoggedIn = checkUserSessionUseCase()

            if (isUserLoggedIn){
                Log.d("SplashVM", "Сессия жива. Идем в Библиотеку.")
                _startDestination.value = Screen.LibraryScreen.route
            }else{
                Log.d("SplashVM", "Сессии нет. Идем логиниться.")
                _startDestination.value = Screen.LoginScreen.route
            }
        }
    }
}
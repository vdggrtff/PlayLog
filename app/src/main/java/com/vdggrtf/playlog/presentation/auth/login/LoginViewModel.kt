package com.vdggrtf.playlog.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.usecase.auth.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)


@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase): ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun login(email: String, password: String) {
        // Сетевой запрос
        viewModelScope.launch {
            // Сбрасываем ошибку перед отправкой запроса и крутим лоадер
            _state.update { it.copy(isLoading = true, error = null) }

            // ВАЖНО: Отправляем на сервер очищенную почту!
            loginUseCase(email, password).fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    // Делаем ошибку от Supabase понятной для пользователя
                    val errorMsg = e.message ?: ""
                    val friendlyError = if (errorMsg.contains("Invalid login credentials", ignoreCase = true)) {
                        "Неверный email или пароль"
                    } else {
                        "Ошибка входа: $errorMsg"
                    }

                    _state.update { it.copy(isLoading = false, error = friendlyError) }
                }
            )
        }
    }

    fun clearError(){
        _state.update { it.copy(error = null) }
    }
}
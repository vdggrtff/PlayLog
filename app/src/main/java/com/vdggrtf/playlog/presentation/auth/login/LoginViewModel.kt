package com.vdggrtf.playlog.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.repository.AuthRepository
import com.vdggrtf.playlog.utils.validators.Validators
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
class LoginViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun login(email: String, password: String) {
        // 1. Очищаем почту от случайных пробелов в начале и в конце
        val cleanEmail = email.trim()

        // 2. Проверка на пустоту
        if (cleanEmail.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Заполните все поля") }
            return
        }

        // 3. Проверка формата почты (ВАЖНО: передаем cleanEmail!)
        if (!Validators.isValidEmail(cleanEmail)) {
            _state.update { it.copy(error = "Некорректный формат email") }
            return
        }

        // 4. Сетевой запрос
        viewModelScope.launch {
            // Сбрасываем ошибку перед отправкой запроса и крутим лоадер
            _state.update { it.copy(isLoading = true, error = null) }

            // ВАЖНО: Отправляем на сервер очищенную почту!
            repository.login(cleanEmail, password).fold(
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
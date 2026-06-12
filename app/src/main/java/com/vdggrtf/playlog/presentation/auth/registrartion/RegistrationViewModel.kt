package com.vdggrtf.playlog.presentation.auth.registrartion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.repository.AuthRepository
import com.vdggrtf.playlog.utils.validators.Validators.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state = _state.asStateFlow()

    fun registration(email: String, password: String, name: String){

        val cleanEmail = email.trim()
        val cleanName = name.trim()

        if (cleanName.isBlank() || cleanEmail.isBlank() || password.isBlank()){
            _state.update { it.copy(error = "Заполните все поля") }
            return
        }

        if(!isValidEmail(email)){
            _state.update { it.copy(error = "неккоректный email") }
            return
        }

        if (password.length < 6){
            _state.update { it.copy(error = "Пароль должен содержать не менее 6 символов") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = repository.registerUser(cleanEmail, password, cleanName)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Ошибка регистрации" ) }
                }
            )
        }
    }

    fun clearError(){
        _state.update { it.copy(error = null) }
    }


}
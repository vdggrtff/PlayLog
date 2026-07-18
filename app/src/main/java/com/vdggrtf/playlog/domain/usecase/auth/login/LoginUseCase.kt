package com.vdggrtf.playlog.domain.usecase.auth.login

import com.vdggrtf.playlog.domain.repository.AuthRepository
import com.vdggrtf.playlog.utils.validators.Validators
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(email: String, password: String): Result<Unit> {

        // 1. Очищаем почту от случайных пробелов в начале и в конце
        val cleanEmail = email.trim()

        // 2. Проверка на пустоту
        if (cleanEmail.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please fill in all fields"))
        }

        // 3. Проверка формата почты (ВАЖНО: передаем cleanEmail!)
        if (!Validators.isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Invalid email format"))
        }

        return authRepository.login(email, password)
    }
}
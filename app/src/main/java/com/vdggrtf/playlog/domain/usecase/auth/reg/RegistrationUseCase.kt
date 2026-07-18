package com.vdggrtf.playlog.domain.usecase.auth.reg

import com.vdggrtf.playlog.domain.repository.AuthRepository
import com.vdggrtf.playlog.utils.validators.Validators.isValidEmail
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<Unit> {
        val cleanEmail = email.trim()
        val cleanName = name.trim()

        // Check for empty fields
        if (cleanName.isBlank() || cleanEmail.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Please fill in all fields"))
        }

        // Validate email format
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Invalid email address"))
        }

        // Validate password length
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters long"))
        }

        return authRepository.registerUser(cleanEmail, password, cleanName)
    }
}
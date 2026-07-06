package com.vdggrtf.playlog.domain.usecase

import com.vdggrtf.playlog.domain.repository.AuthRepository
import javax.inject.Inject

// This UseCase is strictly bound to domain logic, no 3rd-party dependencies!
class CheckUserSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): Boolean{
        return repository.isUserSessionActive()
    }
}
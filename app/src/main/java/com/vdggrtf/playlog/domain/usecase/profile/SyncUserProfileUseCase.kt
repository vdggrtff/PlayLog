package com.vdggrtf.playlog.domain.usecase.profile

import com.vdggrtf.playlog.domain.repository.AuthRepository
import javax.inject.Inject

class SyncUserProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.syncUserProfile()
}
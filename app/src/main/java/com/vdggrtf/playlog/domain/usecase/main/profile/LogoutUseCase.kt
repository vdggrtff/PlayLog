package com.vdggrtf.playlog.domain.usecase.main.profile

import com.vdggrtf.playlog.domain.repository.AuthRepository
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke() {
        repository.logout()
        libraryRepository.clearLocalDatabase()
    }
}
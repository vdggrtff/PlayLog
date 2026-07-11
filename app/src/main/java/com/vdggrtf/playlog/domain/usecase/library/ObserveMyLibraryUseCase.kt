package com.vdggrtf.playlog.domain.usecase.library

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveMyLibraryUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    operator fun invoke(): Flow<List<GameModel>> {
        return repository.getMyLibrary() // Всё! Гениально и просто.
    }
}
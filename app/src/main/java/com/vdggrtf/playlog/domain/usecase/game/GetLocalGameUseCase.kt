package com.vdggrtf.playlog.domain.usecase.game

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import javax.inject.Inject

class GetLocalGameUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(gameId: Int): GameModel? {
        return libraryRepository.getLocalGameById(gameId)
    }
}
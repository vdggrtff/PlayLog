package com.vdggrtf.playlog.domain.usecase.main.playlist

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservePlaylistGamesUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    operator fun invoke(playlistId: String): Flow<List<GameModel>> {
        // Возвращаем реактивный поток игр для конкретного плейлиста
        return libraryRepository.getGamesForPlaylist(playlistId)
    }
}
package com.vdggrtf.playlog.domain.usecase.main.playlist

import com.vdggrtf.playlog.domain.repository.PlaylistRepository
import jakarta.inject.Inject

class RemoveGameFromPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String, gameId: Int): Result<Unit> {
        return repository.removeGameFromPlaylist(playlistId, gameId)
    }
}
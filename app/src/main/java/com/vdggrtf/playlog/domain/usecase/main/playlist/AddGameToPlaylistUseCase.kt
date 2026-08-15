package com.vdggrtf.playlog.domain.usecase.main.playlist

import com.vdggrtf.playlog.domain.repository.PlaylistRepository
import javax.inject.Inject

class AddGameToPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {

    suspend operator fun invoke(playlistId: String, gameId: Int): Result<Unit> {
        return repository.addGameToPlaylist(playlistId = playlistId, gameId = gameId)
    }

}
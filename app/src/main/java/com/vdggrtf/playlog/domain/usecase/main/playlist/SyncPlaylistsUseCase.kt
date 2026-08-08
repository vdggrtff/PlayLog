package com.vdggrtf.playlog.domain.usecase.main.playlist

import com.vdggrtf.playlog.domain.repository.PlaylistRepository
import jakarta.inject.Inject

class SyncPlaylistsUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke() = repository.syncPlaylists()
}
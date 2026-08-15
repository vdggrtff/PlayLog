package com.vdggrtf.playlog.domain.usecase.main.playlist

import com.vdggrtf.playlog.domain.model.PlaylistModel
import com.vdggrtf.playlog.domain.repository.PlaylistRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveMyPlaylistsUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    operator fun invoke(): Flow<List<PlaylistModel>> = repository.observeMyPlaylists()
}
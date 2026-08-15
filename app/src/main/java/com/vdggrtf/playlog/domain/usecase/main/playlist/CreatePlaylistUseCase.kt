package com.vdggrtf.playlog.domain.usecase.main.playlist

import com.vdggrtf.playlog.domain.repository.PlaylistRepository
import jakarta.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(title: String, description: String): Result<Unit> {
        if (title.isBlank()) return Result.failure(Exception("Title cannot be empty"))
        return repository.createPlaylist(title, description)
    }
}
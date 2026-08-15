package com.vdggrtf.playlog.domain.repository

import com.vdggrtf.playlog.domain.model.PlaylistModel
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    // 1. Оффлайн-наблюдение за моими плейлистами (из Room)
    fun observeMyPlaylists(): Flow<List<PlaylistModel>>

    // 2. Скачивание актуальных плейлистов из облака (Синхронизация)
    suspend fun syncPlaylists(): Result<Unit>

    // 3. Создание нового плейлиста
    suspend fun createPlaylist(title: String, description: String): Result<Unit>

    suspend fun addGameToPlaylist(playlistId: String, gameId: Int): Result<Unit>

    suspend fun removeGameFromPlaylist(playlistId: String, gameId: Int): Result<Unit>
}
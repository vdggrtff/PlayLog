package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.local.dao.PlaylistDao
import com.vdggrtf.playlog.data.local.entity.PLAYLIST_DB_NAME
import com.vdggrtf.playlog.data.local.entity.PlaylistGameCrossRef
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.mapper.toEntity
import com.vdggrtf.playlog.data.network.dto.supabase.playlist.PlaylistDto
import com.vdggrtf.playlog.data.network.dto.supabase.playlist.PlaylistGameDto
import com.vdggrtf.playlog.domain.model.PlaylistModel
import com.vdggrtf.playlog.domain.repository.PlaylistRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
    private val playlistDao: PlaylistDao
): PlaylistRepository {

    // РЕАКТИВНОЕ ЧТЕНИЕ (UI всегда слушает эту функцию)
    override fun observeMyPlaylists(): Flow<List<PlaylistModel>> {
        return playlistDao.getMyPlaylists().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    // СИНХРОНИЗАЦИЯ (Идем в облако, качаем, сохраняем локально)
    override suspend fun syncPlaylists(): Result<Unit> {
        return try {
            val user = supabase.auth.currentUserOrNull() ?: return Result.failure(Exception("Not logged in"))

            val dtos = supabase.from(PLAYLIST_DB_NAME)
                .select {
                    filter { eq("creator_id", user.id) }
                }.decodeList<PlaylistDto>()

            val entities = dtos.map { it.toEntity(currentUserId = user.id, gamesCount = 0) }

            playlistDao.insertPlaylists(entities)

            Result.success(Unit)
        } catch (e: Exception){
            Log.e("PlaylistRepo", "Ошибка синхронизации плейлистов: ${e.message}")
            Result.failure(e)
        }

    }

    // СОЗДАНИЕ (Сразу пишем в облако и в локальную базу)
    override suspend fun createPlaylist(
        title: String,
        description: String,
    ): Result<Unit> {
        return try {
            val user = supabase.auth.currentUserOrNull() ?: return Result.failure(Exception("Not logged in"))

            val newPlayList = PlaylistDto(
                id = UUID.randomUUID().toString(),
                creatorId = user.id,
                title = title,
                description = description,
                imageUrl = null,
                isOfficial = false
            )

            supabase.from(PLAYLIST_DB_NAME).insert(newPlayList)

            playlistDao.insertPlaylist(newPlayList.toEntity(user.id))

            Result.success(Unit)
        } catch (e: Exception){
            Log.e("PlaylistRepo", "Ошибка создания плейлиста: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun addGameToPlaylist(playlistId: String, gameId: Int): Result<Unit> {
        return try {
            // 1. Отправляем в облако (Supabase) в таблицу-связку
            val dto = PlaylistGameDto(playlistId = playlistId, gameIdRawg = gameId)
            supabase.from("playlist_games").insert(dto)

            // 2. Сразу сохраняем локально в Room (чтобы работало оффлайн и UI обновился)
            val crossRef = PlaylistGameCrossRef(playlistId = playlistId, gameId = gameId)
            playlistDao.insertPlaylistGames(listOf(crossRef))

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PlaylistRepo", "Ошибка добавления игры в плейлист: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun removeGameFromPlaylist(playlistId: String, gameId: Int): Result<Unit> {
        return try {
            // 1. Удаляем из облака Supabase
            supabase.from("playlist_games").delete {
                filter {
                    eq("playlist_id", playlistId)
                    eq("game_id_rawg", gameId)
                }
            }
            // 2. Удаляем из Room
            playlistDao.removeGameFromPlaylist(playlistId, gameId)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PlaylistRepo", "Ошибка удаления игры: ${e.message}")
            Result.failure(e)
        }
    }
}
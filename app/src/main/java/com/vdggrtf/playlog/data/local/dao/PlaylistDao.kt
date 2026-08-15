package com.vdggrtf.playlog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vdggrtf.playlog.data.local.entity.PlaylistEntity
import com.vdggrtf.playlog.data.local.entity.PlaylistGameCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // --- ПЛЕЙЛИСТЫ ---
    @Query("SELECT * FROM playlists ORDER BY isOfficial DESC, title ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE isMine = 1")
    fun getMyPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: String)

    @Query("DELETE FROM playlist_games WHERE playlistId = :playlistId AND gameId = :gameId")
    suspend fun removeGameFromPlaylist(playlistId: String, gameId: Int)

    @Query("DELETE FROM playlists")
    suspend fun clearAllPlaylists()

    // --- СВЯЗКИ ИГР ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistGames(crossRefs: List<PlaylistGameCrossRef>)

    @Query("SELECT gameId FROM playlist_games WHERE playlistId = :playlistId")
    suspend fun getGamesForPlaylist(playlistId: String): List<Int>

    @Query("DELETE FROM playlist_games WHERE playlistId = :playlistId")
    suspend fun clearGamesForPlaylist(playlistId: String)
}
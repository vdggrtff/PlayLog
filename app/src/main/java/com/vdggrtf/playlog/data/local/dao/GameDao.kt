package com.vdggrtf.playlog.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vdggrtf.playlog.data.local.entity.GAME_DB_NAME
import com.vdggrtf.playlog.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM $GAME_DB_NAME")
    fun getMyLibrary(): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addGame(game: GameEntity)

    @Delete
    fun deleteGame(game: GameEntity)

    @Query("SELECT * FROM $GAME_DB_NAME WHERE id = :gameId")
    fun getGameById(gameId: Int): Flow<GameEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(games: List<GameEntity>)

    @Query("DELETE FROM games_library")
    fun clearAllGames()

    @Query("SELECT * FROM $GAME_DB_NAME WHERE id = :gameId LIMIT 1")
    fun getGameByIdSync(gameId: Int): GameEntity?

    @Query("""
        SELECT games_library.* FROM games_library 
        INNER JOIN playlist_games ON games_library.id = playlist_games.gameId 
        WHERE playlist_games.playlistId = :playlistId
    """)
    fun getGamesForPlaylist(playlistId: String): Flow<List<GameEntity>>
}
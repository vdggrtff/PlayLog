package com.vdggrtf.playlog.domain.repository

import com.vdggrtf.playlog.domain.model.GameModel
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    fun getMyLibrary(): Flow<List<GameModel>>

    suspend fun addGameToLibrary(gameModel: GameModel)

    suspend fun deleteGameFromLibrary(gameModel: GameModel)

    suspend fun getLocalGameById(id: Int): GameModel?

    suspend fun clearLocalDatabase()
}
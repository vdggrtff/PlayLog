package com.vdggrtf.playlog.domain.repository

import com.vdggrtf.playlog.data.network.dto.AchievementDto
import com.vdggrtf.playlog.data.network.dto.CashedGameDto
import com.vdggrtf.playlog.data.network.dto.CheapSharkDealDto
import com.vdggrtf.playlog.domain.model.GameModel

interface GameRepository {

    suspend fun searchGames(query: String, page: Int = 1): Result<List<GameModel>>

    suspend fun getPopularGames(page: Int = 1): Result<List<GameModel>>

    suspend fun getGameDetails(id: Int): Result<GameModel>

    suspend fun getScreenshots(id: Int): Result<List<String>>

    suspend fun getGameAchievements(id: Int): Result<List<AchievementDto>>

    suspend fun getCachedGame(id: Int): CashedGameDto?

    suspend fun getGamePrices(gameName: String): Result<List<CheapSharkDealDto>>

    suspend fun saveToCache(cacheDto: CashedGameDto)
}
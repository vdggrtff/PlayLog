package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.data.network.dto.retro_achievements.RaGameDto
import com.vdggrtf.playlog.data.network.response.RaGameExtendedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetroAchievementsApi {

    // 1. Ищем игру по названию (чтобы узнать её RA ID)
    // У них нет прямого поиска по всем консолям сразу, но мы выкрутимся!
    @GET("API/API_GetGameList.php")
    suspend fun getGamesForConsole(
        @Query("i") consoleId: Int
    ): Response<List<RaGameDto>>

    // 2. Получаем ачивки по RA ID
    @GET("API/API_GetGameExtended.php")
    suspend fun getGameAchievements(
        @Query("i") gameId: Int
    ): Response<RaGameExtendedResponse>
}
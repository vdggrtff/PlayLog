package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.data.network.dto.rawg.GameDto
import com.vdggrtf.playlog.data.network.response.AchievementResponse
import com.vdggrtf.playlog.data.network.response.BaseResponse
import com.vdggrtf.playlog.data.network.response.ScreenshotResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RawgApi {

    @GET("games")
    suspend fun getPopularGames(
        @Query("order") order: String = "-added",
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 40,
        @Query("dates") dates: String? = null,
        @Query("genres") genres: String? = null,
        @Query("parent_platforms") platforms: String? = null
    ): Response<BaseResponse<GameDto>>

    @GET("games")
    suspend fun searchGames(
        @Query("search") query: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 40,
        @Query("dates") dates: String? = null,
        @Query("genres") genres: String? = null,
        @Query("parent_platforms") platforms: String? = null
    ): Response<BaseResponse<GameDto>>

    @GET("games/{id}")
    suspend fun getGameDetails(
        @Path("id") gameId: Int,
    ): Response<GameDto>

    @GET("games/{id}/achievements")
    suspend fun getGameAchievements(
        @Path("id") gameId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 40,
    ): Response<AchievementResponse>

    @GET("games/{id}/screenshots")
    suspend fun getScreenshots(
        @Path("id") gameId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 40,
    ): Response<ScreenshotResponse>
}
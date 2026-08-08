package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.data.network.dto.igdb.IgdbGameDto
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IgdbApi {

    // В IGDB поиск, популярное и списки делаются через один единственный эндпоинт!
    // Вся разница только в тексте `query`, который мы туда отправим.
    @POST("v4/games")
    suspend fun getGames(@Body body: RequestBody): Response<List<IgdbGameDto>>

}
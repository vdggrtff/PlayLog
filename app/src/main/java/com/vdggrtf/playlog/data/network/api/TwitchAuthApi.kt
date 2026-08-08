package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.data.network.response.TwitchTokenResponse
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface TwitchAuthApi {
    @POST("oauth2/token")
    fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "client_credentials"
    ): Call<TwitchTokenResponse>
}
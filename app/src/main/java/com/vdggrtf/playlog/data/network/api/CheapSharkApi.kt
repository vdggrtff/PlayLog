package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.data.network.dto.cheapshark.CheapSharkDealDto
import com.vdggrtf.playlog.data.network.dto.cheapshark.CheapSharkGameDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CheapSharkApi {
    @GET("games")
    suspend fun searchGameDeals(
        @Query("title") title: String,
        @Query("limit") limit: Int = 1,
    ): Response<List<CheapSharkGameDto>>

    @GET("deals")
    suspend fun getStoreSpecificDeals(
        @Query("title") title: String,
        @Query("storeID") storeIds: String = "7,11",
        @Query("sortBy") sortBy: String = "Price",
        @Query("limit") limit: Int = 1,
    ): Response<List<CheapSharkDealDto>>
}
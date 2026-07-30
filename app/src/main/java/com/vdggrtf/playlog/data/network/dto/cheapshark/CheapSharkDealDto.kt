package com.vdggrtf.playlog.data.network.dto.cheapshark

import com.google.gson.annotations.SerializedName

data class CheapSharkDealDto(
    @SerializedName("title") val title: String,
    @SerializedName("salePrice") val salePrice: String,
    @SerializedName("normalPrice") val normalPrice: String,
    @SerializedName("storeID") val storeId: String,
)

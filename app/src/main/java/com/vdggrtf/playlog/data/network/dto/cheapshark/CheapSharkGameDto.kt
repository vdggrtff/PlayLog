package com.vdggrtf.playlog.data.network.dto.cheapshark

import com.google.gson.annotations.SerializedName

data class CheapSharkGameDto(
    @SerializedName("gameID") val gameId: String,
    @SerializedName("external") val name: String,
    @SerializedName("cheapest") val cheapestPrice: String,
    @SerializedName("cheapestDealID") val dealId: String,
)
package com.vdggrtf.playlog.data.network.dto.retro_achievements

import com.google.gson.annotations.SerializedName

data class RaAchievementDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Points") val points: Int,
    @SerializedName("BadgeName") val badgeName: String
)
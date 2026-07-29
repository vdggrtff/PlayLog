package com.vdggrtf.playlog.data.network.dto.rawg

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AchievementDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("percent") val percent: Double? = null,
)
package com.vdggrtf.playlog.data.network.response

import com.vdggrtf.playlog.data.network.dto.AchievementDto
import com.google.gson.annotations.SerializedName

data class AchievementResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("results") val results: List<AchievementDto>,
)

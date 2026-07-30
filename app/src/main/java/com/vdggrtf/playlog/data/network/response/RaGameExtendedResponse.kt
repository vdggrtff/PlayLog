package com.vdggrtf.playlog.data.network.response

import com.google.gson.annotations.SerializedName
import com.vdggrtf.playlog.data.network.dto.retro_achievements.RaAchievementDto

data class RaGameExtendedResponse(
    @SerializedName("ID") val id: Int,
    @SerializedName("Achievements") val achievements: Map<String, RaAchievementDto>?
)
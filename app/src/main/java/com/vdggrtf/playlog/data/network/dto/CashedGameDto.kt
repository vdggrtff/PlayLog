package com.vdggrtf.playlog.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CashedGameDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("released_date") val releasedDate: String?,
    @SerialName("rating") val rating: Double?,
    @SerialName("description") val description: String?,
    @SerialName("screenshots") val screenshots: List<String>? = null,
    @SerialName("achievements") val achievements: List<AchievementDto>? = null,
    @SerialName("ai_difficulty") val aiDifficulty: String? = null,
    @SerialName("genres") val genres: String? = null,
    @SerialName("platforms") val platforms: String? = null
) {
}
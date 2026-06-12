package com.vdggrtf.playlog.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseGameDto(
    @SerialName("user_id") val userId: String,
    @SerialName("game_id_rawg") val gameIdRawg: Int,
    @SerialName("name") val name: String,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("status") val status: String,
    @SerialName("rating") val rating: Double?,
    @SerialName("released_date") val releasedDate: String?,
    @SerialName("description") val description: String? = null,
    @SerialName("ai_difficulty") val aiDifficulty: String,
    @SerialName("user_difficulty") val userDifficulty: String,
    @SerialName("verified_difficulty") val verifiedDifficulty: String,
)

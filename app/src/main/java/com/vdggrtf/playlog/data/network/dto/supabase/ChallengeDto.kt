package com.vdggrtf.playlog.data.network.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeDto(
    val id: Int,
    @SerialName("game_id") val gameId: Int,
    val title: String,
    val description: String,
    @SerialName("ai_prompt") val aiPrompt: String,
    @SerialName("reward_points") val rewardPoints: Int,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("example_image_url") val exampleImageUrl: String? = null,
    @SerialName("creator_name") val creatorName: String? = null,
    @SerialName("creator_donate_url") val creatorDonateUrl: String? = null,
)

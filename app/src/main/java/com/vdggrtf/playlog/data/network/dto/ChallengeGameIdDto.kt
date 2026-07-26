package com.vdggrtf.playlog.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeGameIdDto(
    val id: Int,
    @SerialName("game_id") val gameId: Int
)

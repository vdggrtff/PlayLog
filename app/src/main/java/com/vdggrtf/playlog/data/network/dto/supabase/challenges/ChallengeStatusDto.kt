package com.vdggrtf.playlog.data.network.dto.supabase.challenges

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO for downloading status from Supabase
@Serializable
data class ChallengeStatusResponseDto(
    @SerialName("challenge_id") val challengeId: Int,
    val status: String
)

// DTO for uploading/updating status in Supabase
@Serializable
data class ChallengeStatusUpdateDto(
    @SerialName("challenge_id") val challengeId: Int,
    val status: String
)
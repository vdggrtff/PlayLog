package com.vdggrtf.playlog.data.network.dto.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompletedBountyDto(
    @SerialName("challenge_id") val challengeId: Int
)
package com.vdggrtf.playlog.data.network.dto.supabase.challenges

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompletedIdDto(
    @SerialName("challenge_id") val challengeId: Int
)
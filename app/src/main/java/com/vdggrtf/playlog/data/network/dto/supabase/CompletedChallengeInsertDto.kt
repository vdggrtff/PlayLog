package com.vdggrtf.playlog.data.network.dto.supabase

import kotlinx.serialization.Serializable

@Serializable
data class CompletedChallengeInsertDto(
    val challenge_id: Int
)
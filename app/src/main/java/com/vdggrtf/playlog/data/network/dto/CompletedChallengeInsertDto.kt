package com.vdggrtf.playlog.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompletedChallengeInsertDto(
    val challenge_id: Int
)
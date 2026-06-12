package com.vdggrtf.playlog.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AiGameRecommendation(
    val gameName: String,
    val reason: String,
)
package com.vdggrtf.playlog.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AiGameResponse(
    val recommendations: List<AiGameRecommendation> = emptyList(),
)

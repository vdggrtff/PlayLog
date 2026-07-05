package com.vdggrtf.playlog.domain.model

data class CustomChallengeModel(
    val id: Int,
    val gameId: Int,
    val title: String,
    val description: String,
    val aiPrompt: String,
    val rewardPoints: Int,
    val isCompleted: Boolean = false,
    val imageUrl: String? = null,
    val exampleImageUrl: String? = null,
    val creatorName: String? = null,
    val creatorDonateUrl: String? = null,
    val status: GameStatus = GameStatus.NONE,
)

package com.vdggrtf.playlog.data.mapper

import com.vdggrtf.playlog.data.network.dto.supabase.challenges.ChallengeDto
import com.vdggrtf.playlog.domain.model.CustomChallengeModel

fun ChallengeDto.toDomainModel(): CustomChallengeModel{
    return CustomChallengeModel(
        id = this.id,
        gameId = this.gameId,
        title = this.title,
        description = this.description,
        aiPrompt = this.aiPrompt,
        rewardPoints = this.rewardPoints,
        isCompleted = false,
        imageUrl = this.imageUrl,
        exampleImageUrl = this.exampleImageUrl,
        creatorName = this.creatorName,
        creatorDonateUrl = this.creatorDonateUrl,
    )
}
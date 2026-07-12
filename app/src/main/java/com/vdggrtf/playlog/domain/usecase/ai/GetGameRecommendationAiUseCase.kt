package com.vdggrtf.playlog.domain.usecase.ai

import com.vdggrtf.playlog.domain.model.AiGameRecommendation
import com.vdggrtf.playlog.domain.repository.AiRepository
import javax.inject.Inject

class GetGameRecommendationAiUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(userRequest: String): List<AiGameRecommendation>{
        return aiRepository.getGameRecommendation(userRequest)
    }
}
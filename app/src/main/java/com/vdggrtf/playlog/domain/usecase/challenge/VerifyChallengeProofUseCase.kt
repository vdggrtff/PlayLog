package com.vdggrtf.playlog.domain.usecase.challenge

import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.ChallengeRepository
import javax.inject.Inject

class VerifyChallengeProofUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val challengeRepository: ChallengeRepository,
) {

    suspend operator fun invoke(
        challenge: CustomChallengeModel,
        imageBytes: ByteArray,
    ): Result<Boolean> {
        return try {
            val isVerified = aiRepository.verifyCustomChallenge(
                imageBytes = imageBytes,
                gameName = challenge.title,
                challengePrompt = challenge.aiPrompt
            )

            if (isVerified){
                challengeRepository.updateChallengeStatus(challenge.id, GameStatus.COMPLETED).getOrThrow()

                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
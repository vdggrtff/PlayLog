package com.vdggrtf.playlog.domain.usecase.main.challenge

import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.ChallengeRepository
import jakarta.inject.Inject

class GetChallengesForGameUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(gameId: Int): Result<List<CustomChallengeModel>> {
        return try {
            val challenges = challengeRepository.getChallengesByGameId(gameId).getOrThrow()

            val statuses = challengeRepository.getUserChallengeStatuses().getOrDefault(emptyMap())

            val mergedList = challenges.map { challenge ->
                val status = statuses[challenge.id] ?: GameStatus.NONE
                challenge.copy(
                    status = status,
                    isCompleted = status == GameStatus.COMPLETED
                )
            }

            Result.success(mergedList)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
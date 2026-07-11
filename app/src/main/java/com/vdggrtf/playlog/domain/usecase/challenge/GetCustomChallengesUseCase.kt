package com.vdggrtf.playlog.domain.usecase.challenge

import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.ChallengeRepository
import jakarta.inject.Inject

class GetCustomChallengesUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {

    suspend operator fun invoke(): Result<List<CustomChallengeModel>>{
        return try {
            // 1. Fetch all global challenges
            val challenges = challengeRepository.getChallenges().getOrThrow()

            // 2. Fetch user's current statuses (Map of challengeId -> GameStatus)
            val statuses = challengeRepository.getUserChallengeStatuses().getOrDefault(emptyMap())

            // 3. Merge them together
            val mergedList = challenges.map {challenge ->
                val status = statuses[challenge.id] ?: GameStatus.NONE
                challenge.copy(
                    status = status,
                    isCompleted = status == GameStatus.COMPLETED
                )
            }
            Result.success(mergedList)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
package com.vdggrtf.playlog.domain.usecase.challenge

import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.ChallengeRepository
import javax.inject.Inject

class UpdateChallengeStatusUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository
) {

    suspend operator fun invoke(challengeId: Int, newStatus: GameStatus): Result<Unit> {
        return challengeRepository.updateChallengeStatus(challengeId, newStatus)
    }
}
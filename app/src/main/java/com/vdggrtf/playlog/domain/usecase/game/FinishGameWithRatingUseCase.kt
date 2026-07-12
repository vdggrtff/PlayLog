package com.vdggrtf.playlog.domain.usecase.game

import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import jakarta.inject.Inject

class FinishGameWithRatingUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(userDiff: AchievementDifficulty, game: GameModel, aiDiff: AchievementDifficulty ){
        val completedGame = game.copy(
            status = GameStatus.COMPLETED, // Saving the completion status.
            verifiedDifficulty = aiDiff,
            userDifficulty = userDiff
        )

        libraryRepository.addGameToLibrary(completedGame) // Sending to Room and Supabase.
    }
}
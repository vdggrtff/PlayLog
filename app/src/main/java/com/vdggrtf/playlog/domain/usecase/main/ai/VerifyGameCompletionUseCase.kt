package com.vdggrtf.playlog.domain.usecase.main.ai

import android.util.Log
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import jakarta.inject.Inject

class VerifyGameCompletionUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val libraryRepository: LibraryRepository,
) {
    // Returns Result<Boolean>: true if approved, false if rejected, Failure on error
    suspend operator fun invoke(
        imageBytes: ByteArray,
        game: GameModel,
        aiDifficulty: AchievementDifficulty,  // Passing the difficulty that the AI calculated earlier.
    ): Result<Boolean> {
        return try {
            Log.i("VerificationVM", "[AI Scanner]: Проверка скриншота для ${game.name}")

            val isVerified = aiRepository.verifyScreenshot(imageBytes, game.name)

            if (isVerified) {
                Log.d("VerificationVM", "[AI Scanner]: Скриншот подтвержден! Сохраняем...")
                // AI approved, saving to local library
                val completedGame = game.copy(
                    status = GameStatus.COMPLETED,
                    aiDifficulty = aiDifficulty,
                    verifiedDifficulty = aiDifficulty
                )
                libraryRepository.addGameToLibrary(completedGame)
                Result.success(true)
            } else{
                // AI rejected
                Log.w("VerificationVM", "[AI Scanner]: Скриншот отклонен.")
                Result.success(false)
            }
            }catch (e: Exception){
            Result.failure(e)
            }
    }
        }
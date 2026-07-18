package com.vdggrtf.playlog.domain.usecase.main.game

import android.util.Log
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import jakarta.inject.Inject

class RetryAiEvaluationUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val gameRepository: GameRepository,
) {

    suspend operator fun invoke(gameId: Int, gameName: String): Result<AchievementDifficulty> {
        return try {
            Log.d("GameDetailsVM", "🔄 Пинаем ИИ заново для игры $gameName...")
            val aiDiff = aiRepository.evaluateGameDifficulty(gameName)
            if (aiDiff != AchievementDifficulty.NONE) {
                val cached = gameRepository.getCachedGame(gameId)
                if (cached != null) {
                    gameRepository.saveToCache(cached.copy(aiDifficulty = aiDiff.name))
                    Log.d("RAWG_CACHE", "✅ Успешно обновили сложность в кэше на ${aiDiff.name}")
                }
            }
            Result.success(aiDiff)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
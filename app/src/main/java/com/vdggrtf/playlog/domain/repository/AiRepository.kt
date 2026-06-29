package com.vdggrtf.playlog.domain.repository

import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.AiGameRecommendation

interface AiRepository {
    suspend fun getGameRecommendation(userRequest: String): List<AiGameRecommendation>
    suspend fun evaluateGameDifficulty(gameName: String): AchievementDifficulty
    suspend fun verifyScreenshot(imageBytes: ByteArray, gameName: String): Boolean
    suspend fun scanLibraryForGames(imageBytes: ByteArray): List<String>

    suspend fun saveDifficultyToGlobalCache(gameId: Int, difficulty: AchievementDifficulty)

    suspend fun verifyCustomChallenge(
        imageBytes: ByteArray,
        gameName: String,
        challengePrompt: String
    ): Boolean
}
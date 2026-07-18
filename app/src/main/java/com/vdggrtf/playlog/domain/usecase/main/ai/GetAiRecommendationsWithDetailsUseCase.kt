package com.vdggrtf.playlog.domain.usecase.main.ai

import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.presentation.main.recommendation.ai.AiRecommendedGame
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetAiRecommendationsWithDetailsUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val gameRepository: GameRepository,
) {

    suspend operator fun invoke(userRequest: String): Result<List<AiRecommendedGame>> {
        return try {
            val aiList = aiRepository.getGameRecommendation(userRequest)

            if (aiList.isEmpty()) {
                return Result.failure(Exception("No recommendations found"))
            }

            val gamesWithDetails = coroutineScope {
                aiList.map { aiGame ->
                    async {
                        val searchResult = gameRepository.searchGames(aiGame.gameName, page = 1)
                        val realGame = searchResult.getOrNull()?.firstOrNull()

                        AiRecommendedGame(
                            aiReason = aiGame.reason,
                            gameDetails = realGame
                        )
                    }
                }.awaitAll()
            }
            Result.success(gamesWithDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
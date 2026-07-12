package com.vdggrtf.playlog.domain.usecase.game

import android.util.Log
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.network.dto.CashedGameDto
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.model.RemoteGameData
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FetchAndSyncRemoteGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(id: Int, localGame: GameModel?): Result<RemoteGameData> {
        return try {
            // 1. ПЫТАЕМСЯ ВЗЯТЬ ИЗ ГЛОБАЛЬНОГО КЭША (SUPABASE)
            val cachedDto = gameRepository.getCachedGame(id)
            if (cachedDto != null) {
                Log.d("RAWG_CACHE", "🔥 HIT! ВЗЯЛИ ВСЁ ИЗ КЭША!")
                val cachedGameModel = cachedDto.toDomainModel()

                val aiDiff = try {
                    cachedDto.aiDifficulty?.let { AchievementDifficulty.valueOf(it) } ?: AchievementDifficulty.NONE
                } catch (e: Exception) { AchievementDifficulty.NONE }

                // SMART MERGE #1
                val finalGame = cachedGameModel.copy(
                    status = localGame?.status ?: GameStatus.NONE,
                    aiDifficulty = localGame?.aiDifficulty ?: aiDiff,
                    userDifficulty = localGame?.userDifficulty ?: AchievementDifficulty.NONE,
                    verifiedDifficulty = localGame?.verifiedDifficulty ?: AchievementDifficulty.NONE
                )

                var finalAiDiff = localGame?.aiDifficulty ?: aiDiff

                // Пинаем ИИ, если оценки еще нет
                if (finalAiDiff == AchievementDifficulty.NONE) {
                    try {
                        finalAiDiff = aiRepository.evaluateGameDifficulty(finalGame.name)
                        if (finalAiDiff != AchievementDifficulty.NONE) {
                            gameRepository.saveToCache(cachedDto.copy(aiDifficulty = finalAiDiff.name))
                        }
                    } catch (e: Exception) { /* Игнорируем ошибку ИИ */ }
                }

                return Result.success(
                    RemoteGameData(
                        game = finalGame,
                        screenshots = cachedDto.screenshots ?: emptyList(),
                        achievements = cachedDto.achievements ?: emptyList(),
                        objectiveDifficulty = finalAiDiff
                    )
                )
            }
            // 2. КЭША НЕТ. ИДЕМ В RAWG API
            Log.d("RAWG_CACHE", "🧊 MISS! Качаем из RAWG...")
            coroutineScope {
                val detailsDef = async { gameRepository.getGameDetails(id) }
                val screensDef = async { gameRepository.getScreenshots(id) }
                val achivDef = async { gameRepository.getGameAchievements(id) }

                val detailsResult = detailsDef.await()
                val screens = screensDef.await().getOrNull() ?: emptyList()
                val achivs = achivDef.await().getOrNull() ?: emptyList()

                if (detailsResult.isSuccess) {
                    val networkGame = detailsResult.getOrNull()!!

                    // SMART MERGE #2
                    val mergedGame = networkGame.copy(
                        status = localGame?.status ?: GameStatus.NONE,
                        aiDifficulty = localGame?.aiDifficulty ?: AchievementDifficulty.NONE,
                        userDifficulty = localGame?.userDifficulty ?: AchievementDifficulty.NONE,
                        verifiedDifficulty = localGame?.verifiedDifficulty ?: AchievementDifficulty.NONE
                    )

                    var aiDiff = AchievementDifficulty.NONE
                    try {
                        aiDiff = aiRepository.evaluateGameDifficulty(networkGame.name)
                        // Сохраняем в кэш!
                        val cacheDto = CashedGameDto(
                            id = networkGame.id,
                            name = networkGame.name,
                            imageUrl = networkGame.imageUrl,
                            releasedDate = networkGame.releasedDate,
                            rating = networkGame.rating,
                            description = networkGame.descriptionRaw,
                            screenshots = screens,
                            achievements = achivs,
                            aiDifficulty = if (aiDiff == AchievementDifficulty.NONE) null else aiDiff.name
                        )
                        gameRepository.saveToCache(cacheDto)
                    } catch (e: Exception) { /* Игнорируем */ }

                    Result.success(RemoteGameData(mergedGame, screens, achivs, aiDiff))
                } else {
                    Result.failure(Exception(detailsResult.exceptionOrNull()?.message ?: "Unknown RAWG Error"))
                }
            }
        } catch (e: Exception) {
            Log.e("GameDetails", "ФАТАЛЬНАЯ ОШИБКА: ${e.message}")
            Result.failure(e)
        }
    }
}
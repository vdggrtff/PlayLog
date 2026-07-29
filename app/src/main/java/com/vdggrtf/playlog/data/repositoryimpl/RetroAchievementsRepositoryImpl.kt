package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.network.api.RetroAchievementsApi
import com.vdggrtf.playlog.data.network.dto.rawg.AchievementDto
import com.vdggrtf.playlog.domain.repository.RetroAchievementsRepository
import javax.inject.Inject

class RetroAchievementsRepositoryImpl @Inject constructor(
    private val api: RetroAchievementsApi
): RetroAchievementsRepository {

    override suspend fun getRetroAchievements(
        gameName: String,
        platforms: List<String>,
    ): Result<List<AchievementDto>> {
        return try {
            // Try угадать ID console по platforms game from RAWG
            val consoleId = guessConsoledId(platforms)
            if (consoleId == -1) return Result.success(emptyList())

            // download list all games for this console
            val gameResponse = api.getGamesForConsole(consoleId)
            if (!gameResponse.isSuccessful) return Result.failure(Exception("RA API error"))

            val gamesList = gameResponse.body() ?: emptyList()

            val cleanGameName = gameName.substringBefore(":").trim()

            // Find the game for name
            /*val raGame = gamesList.find { it.title.contains(gameName, ignoreCase = true) }
                ?: return Result.success(emptyList())*/

            val raGame = gamesList.find {
                it.title.contains(gameName, ignoreCase = true) ||
                        it.title.contains(cleanGameName, ignoreCase = true)
            }

            if (raGame == null) {
                Log.w("RetroAchievements", "Игра '$gameName' не найдена на консоли $consoleId! (В базе ${gamesList.size} игр)")
                return Result.success(emptyList())
            }

            // download achievements по finded Id
            val achResponse = api.getGameAchievements(raGame.id)
            if (!achResponse.isSuccessful) return Result.failure(Exception("RA Achievements error"))

            val raAchievements = achResponse.body()?.achievements?.values ?: emptyList()
            val mappedAchievements = raAchievements.map { raAch ->
                AchievementDto(
                    id = raAch.id,
                    name = raAch.title,
                    description = raAch.description,
                    image = "https://retroachievements.org/Badge/${raAch.badgeName}.png",
                    percent = raAch.points.toDouble()
                )
            }
            Result.success(mappedAchievements)
        }catch (e: Exception) {
            Log.e("RetroAchievements", "Ошибка: ${e.message}")
            Result.failure(e)
        }
    }

    private fun guessConsoledId(platforms: List<String>): Int{
        val platformsStr = platforms.joinToString(" ").lowercase()
        return when{
            platformsStr.contains("sega") || platformsStr.contains("genesis") || platformsStr.contains("mega drive") -> 1
            platformsStr.contains("nintendo") || platformsStr.contains("snes") -> 3
            platformsStr.contains("game boy advance") || platformsStr.contains("gba") -> 5
            platformsStr.contains("nes") || platformsStr.contains("famicom") -> 7
            platformsStr.contains("playstation") || platformsStr.contains("ps1") -> 12
            else -> -1
        }
    }
}
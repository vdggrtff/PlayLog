package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.network.api.RetroAchievementsApi
import com.vdggrtf.playlog.data.network.dto.rawg.AchievementDto
import com.vdggrtf.playlog.domain.repository.RetroAchievementsRepository
import javax.inject.Inject

class RetroAchievementsRepositoryImpl @Inject constructor(
    private val api: RetroAchievementsApi,
) : RetroAchievementsRepository {

    override suspend fun getRetroAchievements(
        gameName: String,
        platforms: List<String>,
    ): Result<List<AchievementDto>> {
        return try {
            // Try угадать ID console по platforms game from RAWG
            val consoleIds = guessConsoledId(platforms)

            if (consoleIds.isEmpty()) {
                Log.d("RetroAchievements", "Пропуск: Игре '$gameName' не нужны ретро-ачивки.")
                return Result.success(emptyList())
            }

            val superCleanName = gameName.substringBefore(":").substringBefore("(").trim()

            for (consoleId in consoleIds) {
                Log.d("RetroAchievements", "Ищем '$superCleanName' на консоли ID: $consoleId...")

                val gameResponse = api.getGamesForConsole(consoleId)
                val gamesList = gameResponse.body() ?: continue

                val raGames = gamesList.find { raGame ->
                    val cleanRaTitle = raGame.title.substringBefore("[").substringBefore("~").trim()
                    cleanRaTitle.equals(superCleanName, ignoreCase = true) || cleanRaTitle.equals(gameName, ignoreCase = true)
                } ?: gamesList.find { raGame ->
                        val cleanRaTitle = raGame.title.substringBefore("[").substringBefore("~").trim()
                        if (cleanRaTitle.contains(superCleanName, ignoreCase = true)) {
                            val leftover = cleanRaTitle.removePrefix(superCleanName).trim()
                            !leftover.matches(Regex("^[0-9IVX]+.*")) // Блокируем цифры после названия
                        } else {
                            false
                        }
                    }
                if (raGames != null){
                   // Log.d("RetroAchievements", "🔥 БИНГО! Нашли игру: ${raGame.title} (ID: ${raGame.id})")

                    val achResponse = api.getGameAchievements(raGames.id)
                    val raAchievements = achResponse.body()?.achievements?.values ?: emptyList()

                    if (raAchievements.isEmpty()) continue

                    val mappedAchievements = raAchievements.map { raAch ->
                        AchievementDto(
                            id = raAch.id,
                            name = raAch.title,
                            description = raAch.description,
                            image = "https://retroachievements.org/Badge/${raAch.badgeName}.png",
                            percent = raAch.points.toDouble()
                        )
                    }
                    return Result.success(mappedAchievements)
                }
                }
            Log.w("RetroAchievements", "❌ Игру '$gameName' не нашли в базе RetroAchievements.")
            Result.success(emptyList())
        } catch (e: Exception) {
            Log.e("RetroAchievements", "Ошибка: ${e.message}")
            Result.failure(e)
        }
    }

    private fun guessConsoledId(platforms: List<String>): List<Int> {
        val ids = mutableSetOf<Int>()
        val pStr = platforms.joinToString(" ").lowercase()

        if (pStr.contains("mega drive") || pStr.contains("genesis") || pStr.contains("sega")) ids.add(
            1
        )
        if (pStr.contains("nintendo 64") || pStr.contains("n64")) ids.add(2)
        if (pStr.contains("snes") || pStr.contains("super nintendo")) ids.add(3)
        if (pStr.contains("game boy") || pStr.contains("gba")) ids.add(5)
        if (pStr.contains("nes") || pStr.contains("famicom")) ids.add(7)
        if (pStr.contains("playstation") || pStr.contains("ps1")) ids.add(12)
        if (pStr.contains("dos") || pStr.contains("ms-dos") || pStr.contains("pc")) ids.add(18)
        if (pStr.contains("32x")) ids.add(11)
        if (pStr.contains("dreamcast")) ids.add(8)

        return ids.toList()
    }
}
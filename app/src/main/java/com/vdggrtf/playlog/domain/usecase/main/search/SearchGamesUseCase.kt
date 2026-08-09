package com.vdggrtf.playlog.domain.usecase.main.search

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class SearchGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1, filters: AdvancedFilters): Result<List<GameModel>>{

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
        val startUnix = (dateFormat.parse("${filters.yearRange.start.toInt()}-01-01")?.time ?: 0L) / 1000L
        val endUnix = (dateFormat.parse("${filters.yearRange.endInclusive.toInt()}-12-31")?.time ?: 0L) / 1000L
        val datesStr = "first_release_date >= $startUnix & first_release_date <= $endUnix"

        val genresStr = if (filters.selectedGenres.isNotEmpty()) {
            filters.selectedGenres.mapNotNull { genre ->
                when (genre.lowercase()) {
                    "action" -> "8"
                    "rpg" -> "12"
                    "shooter" -> "5"
                    "adventure" -> "31"
                    "indie" -> "32"
                    "strategy" -> "11"
                    "puzzle" -> "9"
                    else -> null
                }
            }.joinToString(",")
        } else null

        val platformsStr = if (filters.selectedPlatforms.isNotEmpty()) {
            filters.selectedPlatforms.mapNotNull { platform ->
                when (platform.lowercase()) {
                    "pc" -> "6"
                    "playstation" -> "7,8,9,48,167" // Все PS
                    "xbox" -> "11,12,49,169"        // Все Xbox
                    "nintendo" -> "18,19,20,21,130,137,37"
                    "mobile" -> "34,39"
                    "sega" -> "29,35,30"
                    "atari" -> "59,60"
                    else -> null
                }
            }.joinToString(",")
        } else null

        val result = repository.searchGames(query, page, datesStr, genresStr, platformsStr)

        return result.map { games ->
            games.filter { game ->
                val rating = game.rating?.toFloat() ?: 0f
                val minScore = filters.ratingRange.start * 20f
                val maxScore = filters.ratingRange.endInclusive * 20f

                rating in minScore..maxScore
            }
        }
    }
}
package com.vdggrtf.playlog.domain.usecase.main.search

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import javax.inject.Inject

class SearchGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1, filters: AdvancedFilters): Result<List<GameModel>>{

        val startYear = filters.yearRange.start.toInt()
        val endYear = filters.yearRange.endInclusive.toInt()
        val datesStr = "$startYear-01-01,$endYear-12-31"

        val genresStr = if (filters.selectedGenres.isNotEmpty()){
            filters.selectedGenres.mapNotNull { genre ->
                when(genre){
                    "Action" -> "action"
                    "RPG" -> "role-playing-games"
                    "Shooter" -> "shooter"
                    "Adventure" -> "adventure"
                    "Indie" -> "indie"
                    "Strategy" -> "strategy"
                    "Puzzle" -> "puzzle"
                    else -> null
                }
            }.joinToString(",")
        }else null

        val platformsStr = if (filters.selectedPlatforms.isNotEmpty()){
            filters.selectedPlatforms.mapNotNull { platform ->
                when (platform.lowercase()){
                    "pc" -> "1"
                    "playstation" -> "2"
                    "xbox" -> "3"
                    "nintendo" -> "7"
                    "mobile" -> "4,8"
                    "sega" -> "11"
                    "atari" -> "9"
                    else -> null
                }
            }.joinToString(",")
        } else null

        val result = repository.searchGames(query, page, datesStr, genresStr, platformsStr)

        return result.map { games ->
            games.filter { game ->
                val rating = game.rating?.toFloat() ?: 0f
                rating >= filters.ratingRange.start && rating <= filters.ratingRange.endInclusive
            }
        }
    }
}
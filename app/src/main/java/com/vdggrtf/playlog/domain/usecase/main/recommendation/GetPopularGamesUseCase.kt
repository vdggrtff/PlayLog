package com.vdggrtf.playlog.domain.usecase.main.recommendation

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import javax.inject.Inject

// The UseCase represents a single specific business action.
// It sits between the ViewModel and the Repository.
class GetPopularGamesUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    // Overriding the 'invoke' operator allows us to call the class like a function:
    // getPopularGamesUseCase(page = 1)
    suspend operator fun invoke(page: Int = 1, filters: AdvancedFilters): Result<List<GameModel>> {

        val startYear = filters.yearRange.start.toInt()
        val endYear = filters.yearRange.endInclusive.toInt()
        val datesStr = "$startYear-01-01,$endYear-12-31"

        val genresStr = if (filters.selectedGenres.isNotEmpty()){
            filters.selectedGenres.mapNotNull { genre ->
                when (genre){
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
        } else null

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

        val result = repository.getPopularGames(
            page = page,
            dates = datesStr,
            genres = genresStr,
            platforms = platformsStr
        )

        return result.map {games ->
            games.filter { game ->
                val rating = game.rating?.toFloat() ?: 0f
                rating in filters.ratingRange
            }
        }
    }
}
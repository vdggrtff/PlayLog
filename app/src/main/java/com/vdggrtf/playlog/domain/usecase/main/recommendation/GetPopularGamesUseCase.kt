package com.vdggrtf.playlog.domain.usecase.main.recommendation

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import javax.inject.Inject

// The UseCase represents a single specific business action.
// It sits between the ViewModel and the Repository.
class GetPopularGamesUseCase @Inject constructor(
    private val repository: GameRepository,
) {
    // Overriding the 'invoke' operator allows us to call the class like a function:
    // getPopularGamesUseCase(page = 1)
    suspend operator fun invoke(page: Int = 1): Result<List<GameModel>> {
        return repository.getPopularGames(page = page)
    }
}
package com.vdggrtf.playlog.domain.usecase

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import javax.inject.Inject

class SearchGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<GameModel>>{
        return repository.searchGames(query = query, page = page)
    }
}
package com.vdggrtf.playlog.domain.usecase.main.recommendation

import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import jakarta.inject.Inject

class GetIndieGamesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<GameModel>> {
        // IGDB: Жанр 32 = Indie. Год >= 2020
        val datesStr = "first_release_date >= 1577836800"
        val genresStr = "32"

        // Отправляем жестко зашитые параметры на сервер
        return repository.getPopularGames(
            page = page,
            dates = datesStr,
            genres = genresStr,
            platforms = null
        )
    }
}
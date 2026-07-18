package com.vdggrtf.playlog.domain.usecase.main.game

import com.vdggrtf.playlog.domain.repository.GameRepository
import jakarta.inject.Inject

class GetBestGameDealUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(gameName: String): String? {
        val deals = repository.getGamePrices(gameName).getOrNull()

        if (deals.isNullOrEmpty()) return null

        val exactDeal = deals.firstOrNull { deal ->
            deal.title.equals(gameName, ignoreCase = true)
        } ?: deals.firstOrNull { deal ->
            !deal.title.contains("DLC", ignoreCase = true) &&
                    !deal.title.contains("Pass", ignoreCase = true) &&
                    !deal.title.contains("Upgrade", ignoreCase = true) &&
                    !deal.title.contains("Expansion", ignoreCase = true)
        } ?: deals.first()

        return exactDeal.salePrice
    }
}
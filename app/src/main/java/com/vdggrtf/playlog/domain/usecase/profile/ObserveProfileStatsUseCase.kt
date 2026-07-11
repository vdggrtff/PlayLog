package com.vdggrtf.playlog.domain.usecase.profile

import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ProfileStats(
    val totalGames: Int,
    val completedGames: Int,
    val customChallengeCount: Int,
    val peakDifficulty: String
)

class ObserveProfileStatsUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    // 💥 ТВОЙ ТОЧНЫЙ КОД ПОДСЧЕТА!
    operator fun invoke(): Flow<ProfileStats> {
        return repository.getMyLibrary().map { games ->
            val total = games.size
            val completed = games.count { it.status == GameStatus.COMPLETED }
            val custom = games.count { it.verifiedDifficulty == AchievementDifficulty.CUSTOM_CHALLENGE }
            val peakDiff = games
                .filter { it.status == GameStatus.COMPLETED && it.verifiedDifficulty != AchievementDifficulty.NONE }
                .maxByOrNull { it.verifiedDifficulty.ordinal }
                ?.verifiedDifficulty?.title ?: "N/A"

            ProfileStats(total, completed, custom, peakDiff)
        }
    }
}
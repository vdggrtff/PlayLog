package com.vdggrtf.playlog.domain.usecase.game

import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import jakarta.inject.Inject

class GetCommunityRatingUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {

    suspend operator fun invoke(gameId: Int): Pair<AchievementDifficulty, Int> {
        val votes = libraryRepository.getCommunityDifficulties(gameId).getOrDefault(emptyList())

        if (votes.isEmpty()) return Pair(AchievementDifficulty.NONE, 0)

        val popularVoteString = votes
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }?.key

        val communityDifficulty = try {
            AchievementDifficulty.valueOf(popularVoteString ?: "NONE")
        } catch (e: Exception){
            AchievementDifficulty.NONE
        }

        return Pair(communityDifficulty, votes.size)
    }
}
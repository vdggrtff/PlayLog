package com.vdggrtf.playlog.domain.model

data class GameModel(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val rating: Double?,
    val releasedDate: String?,
    val status: GameStatus,
    val unlockedAchievements: Int = 0,
    val totalAchievements: Int = 0,
    val descriptionRaw: String? = null,
    val playtime: Int = 0,
    val aiDifficulty: AchievementDifficulty = AchievementDifficulty.NONE,
    val userDifficulty: AchievementDifficulty = AchievementDifficulty.NONE,
    val verifiedDifficulty: AchievementDifficulty = AchievementDifficulty.NONE,
)
package com.vdggrtf.playlog.domain.model

import com.vdggrtf.playlog.data.network.dto.AchievementDto

data class RemoteGameData(
    val game: GameModel,
    val screenshots: List<String>,
    val achievements: List<AchievementDto>,
    val objectiveDifficulty: AchievementDifficulty
)
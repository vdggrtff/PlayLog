package com.vdggrtf.playlog.domain.repository

import com.vdggrtf.playlog.data.network.dto.rawg.AchievementDto

interface RetroAchievementsRepository {

    suspend fun getRetroAchievements(gameName: String, platforms: List<String>): Result<List<AchievementDto>>
}
package com.vdggrtf.playlog.data.mapper

import com.vdggrtf.playlog.data.local.entity.GameEntity
import com.vdggrtf.playlog.data.network.dto.CashedGameDto
import com.vdggrtf.playlog.data.network.dto.GameDto
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus

fun GameDto.toDomainModel(): GameModel {
    return GameModel(
        id = this.id,
        name = this.name,
        releasedDate = this.released,
        imageUrl = this.backgroundImage,
        status = GameStatus.NONE,
        rating = this.rating,
        unlockedAchievements = 0,
        totalAchievements = 0,
        descriptionRaw = this.description,
        playtime = this.playtime ?: 0,
        aiDifficulty = AchievementDifficulty.NONE,
        userDifficulty = AchievementDifficulty.NONE,
        verifiedDifficulty = AchievementDifficulty.NONE,
    )
}

fun GameEntity.toDomainModel(): GameModel {
    return GameModel(
        id = this.id,
        name = this.name,
        releasedDate = this.releasedDate,
        imageUrl = this.imageUrl,
        status = try {
            GameStatus.valueOf(status)
        } catch (e: Exception) {
            GameStatus.NONE
        },
        rating = this.rating,
        unlockedAchievements = this.unlockedAchievements,
        totalAchievements = this.totalAchievements,
        descriptionRaw = this.descriptionRaw,
        playtime = this.playtime,
        aiDifficulty = try {
            AchievementDifficulty.valueOf(this.aiDifficulty)
        } catch (e: Exception) {
            AchievementDifficulty.NONE
        },
        userDifficulty = try {
            AchievementDifficulty.valueOf(this.userDifficulty)
        } catch (e: Exception) {
            AchievementDifficulty.NONE
        },
        verifiedDifficulty = try {
            AchievementDifficulty.valueOf(this.verifiedDifficulty)
        } catch (e: Exception) {
            AchievementDifficulty.NONE
        }
    )
}

fun GameModel.toEntity(): GameEntity {
    return GameEntity(
        id = this.id,
        name = this.name,
        releasedDate = releasedDate ?: "",
        imageUrl = imageUrl ?: "",
        status = status.name,
        rating = rating ?: 0.0,
        unlockedAchievements = unlockedAchievements,
        totalAchievements = totalAchievements,
        descriptionRaw = this.descriptionRaw,
        playtime = this.playtime,
        aiDifficulty = this.aiDifficulty.name,
        userDifficulty = this.userDifficulty.name,
        verifiedDifficulty = this.verifiedDifficulty.name
    )
}

fun CashedGameDto.toDomainModel(): GameModel {
    return GameModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        releasedDate = releasedDate,
        rating = rating,
        descriptionRaw = description,
        status = GameStatus.NONE,
        aiDifficulty = AchievementDifficulty.NONE,
        userDifficulty = AchievementDifficulty.NONE,
        verifiedDifficulty = AchievementDifficulty.NONE,
    )
}
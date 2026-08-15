package com.vdggrtf.playlog.data.mapper

import com.vdggrtf.playlog.data.local.entity.GameEntity
import com.vdggrtf.playlog.data.network.dto.igdb.IgdbGameDto
import com.vdggrtf.playlog.data.network.dto.rawg.GameDto
import com.vdggrtf.playlog.data.network.dto.supabase.SupabaseGameDto
import com.vdggrtf.playlog.data.network.dto.supabase.challenges.CashedGameDto
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun IgdbGameDto.toDomainModel(): GameModel {
    val coverUrl = this.cover?.imageId?.let { "https://images.igdb.com/igdb/image/upload/t_cover_big/$it.jpg" }

    val formattedDate = this.firstReleaseDate?.let { seconds ->
        val date = Date(seconds * 1000L)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    val normalizedRating = this.rating

    return GameModel(
        id = this.id,
        name = this.name,
        releasedDate = formattedDate,
        imageUrl = coverUrl,
        status = GameStatus.NONE,
        rating = normalizedRating,
        unlockedAchievements = 0,
        totalAchievements = 0,
        descriptionRaw = this.summary,
        playtime = 0,
        aiDifficulty = AchievementDifficulty.NONE,
        userDifficulty = AchievementDifficulty.NONE,
        verifiedDifficulty = AchievementDifficulty.NONE,
        genres = this.genres?.map { it.name } ?: emptyList(),
        platforms = this.platforms?.map { it.name } ?: emptyList()
    )
}

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
        genres = this.genres?.map { it.name } ?: emptyList(),
        platforms = this.platforms?.map { it.platform.name } ?: emptyList()
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
        },
        genres = if (this.genresRaw.isNotBlank()) this.genresRaw.split(",") else emptyList(),
        platforms = if (this.platformsRaw.isNotBlank()) this.platformsRaw.split(",") else emptyList(),
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
        verifiedDifficulty = this.verifiedDifficulty.name,
        genresRaw = this.genres.joinToString(","),
        platformsRaw = this.platforms.joinToString(",")
    )
}

fun GameModel.toSupabaseDto(userId: String): SupabaseGameDto {
    return SupabaseGameDto(
        userId = userId,
        gameIdRawg = this.id,
        name = this.name,
        imageUrl = this.imageUrl ?: "",
        status = this.status.name,
        rating = this.rating ?: 0.0,
        releasedDate = this.releasedDate ?: "",
        description = this.descriptionRaw ?: "",
        aiDifficulty = this.aiDifficulty.name,
        userDifficulty = this.userDifficulty.name,
        verifiedDifficulty = this.verifiedDifficulty.name,
        // 💥 Now it's centralized and safe!
        genres = this.genres.joinToString(","),
        platforms = this.platforms.joinToString(",")
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
        genres = this.genres?.split(",")?.filter { it.isNotBlank() }?.map { it.trim() } ?: emptyList(),
        platforms = this.platforms?.split(",")?.filter { it.isNotBlank() }?.map { it.trim() } ?: emptyList()
    )
}
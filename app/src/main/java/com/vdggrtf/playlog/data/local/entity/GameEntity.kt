package com.vdggrtf.playlog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = GAME_DB_NAME)
data class GameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val releasedDate: String,
    val imageUrl: String,
    val rating: Double,
    val status: String,
    val unlockedAchievements: Int,
    val totalAchievements: Int,
    val descriptionRaw: String?,
    val playtime: Int = 0,
    val aiDifficulty: String,
    val userDifficulty: String,
    val verifiedDifficulty: String,
    val genresRaw: String = "",
    val platformsRaw: String = "",
)

const val GAME_DB_NAME = "games_library"
package com.vdggrtf.playlog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PLAYLIST_DB_NAME)
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val isOfficial: Boolean,
    val isMine: Boolean,
    val gamesCount: Int = 0
)

@Entity(
    tableName = PLAYLIST_GAMES_DB_NAME  ,
    primaryKeys = ["playlistId", "gameId"]
)
data class PlaylistGameCrossRef(
    val playlistId: String,
    val gameId: Int
)

const val PLAYLIST_DB_NAME = "playlists"

const val PLAYLIST_GAMES_DB_NAME = "playlist_games"
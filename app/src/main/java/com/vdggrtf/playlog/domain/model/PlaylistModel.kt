package com.vdggrtf.playlog.domain.model

data class PlaylistModel(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val isOfficial: Boolean,
    val isMine: Boolean, // Принадлежит ли этот плейлист текущему юзеру
    val gamesCount: Int = 0
)
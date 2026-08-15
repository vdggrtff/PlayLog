package com.vdggrtf.playlog.data.mapper

import com.vdggrtf.playlog.data.local.entity.PlaylistEntity
import com.vdggrtf.playlog.data.network.dto.supabase.playlist.PlaylistDto
import com.vdggrtf.playlog.domain.model.PlaylistModel

// Из DTO (Облака) в Room (Оффлайн-БД)
fun PlaylistDto.toEntity(currentUserId: String, gamesCount: Int = 0): PlaylistEntity {
    return PlaylistEntity(
        id = this.id,
        title = this.title,
        description = this.description ?: "",
        imageUrl = this.imageUrl ?: "",
        isOfficial = this.isOfficial,
        isMine = this.creatorId == currentUserId, // 💥 Если автор - я, значит плейлист мой!
        gamesCount = gamesCount
    )
}

// Из Room в Доменную модель (Для экрана)
fun PlaylistEntity.toDomainModel(): PlaylistModel {
    return PlaylistModel(
        id = this.id,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl.takeIf { it.isNotBlank() },
        isOfficial = this.isOfficial,
        isMine = this.isMine,
        gamesCount = this.gamesCount
    )
}
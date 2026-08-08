package com.vdggrtf.playlog.data.network.dto.supabase.playlist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    @SerialName("id") val id: String,
    @SerialName("creator_id") val creatorId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String?,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("is_official") val isOfficial: Boolean
)
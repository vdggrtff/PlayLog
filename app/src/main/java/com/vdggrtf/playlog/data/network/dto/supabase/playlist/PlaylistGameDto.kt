package com.vdggrtf.playlog.data.network.dto.supabase.playlist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistGameDto(
    @SerialName("playlist_id") val playlistId: String,
    @SerialName("game_id_rawg") val gameIdRawg: Int
)
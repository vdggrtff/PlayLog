package com.vdggrtf.playlog.data.network.dto.retro_achievements

import com.google.gson.annotations.SerializedName

data class RaGameDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("ConsoleID") val consoleId: Int
)
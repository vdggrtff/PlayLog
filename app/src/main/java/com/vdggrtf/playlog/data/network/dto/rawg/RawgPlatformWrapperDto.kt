package com.vdggrtf.playlog.data.network.dto.rawg

import com.google.gson.annotations.SerializedName

data class RawgPlatformWrapperDto(
    @SerializedName("platform") val platform: RawgPlatformDto
)
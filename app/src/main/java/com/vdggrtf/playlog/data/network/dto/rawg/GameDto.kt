package com.vdggrtf.playlog.data.network.dto.rawg

import com.google.gson.annotations.SerializedName

data class GameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String?,
    @SerializedName("background_image") val backgroundImage: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("description_raw") val description: String?,
    @SerializedName("playtime") val playtime: Int?,
    @SerializedName("genres") val genres: List<RawgGenreDto>? = null,
    @SerializedName("platforms") val platforms: List<RawgPlatformWrapperDto>? = null
)
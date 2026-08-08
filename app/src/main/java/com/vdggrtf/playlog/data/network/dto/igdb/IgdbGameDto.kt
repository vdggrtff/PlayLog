package com.vdggrtf.playlog.data.network.dto.igdb

import com.google.gson.annotations.SerializedName

data class IgdbGameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("summary") val summary: String?, // Описание
    @SerializedName("rating") val rating: Double?,   // Рейтинг (0 - 100)
    @SerializedName("first_release_date") val firstReleaseDate: Long?, // Дата в секундах (Unix)
    @SerializedName("cover") val cover: IgdbImageDto?,
    @SerializedName("genres") val genres: List<IgdbNameDto>? = null,
    @SerializedName("platforms") val platforms: List<IgdbNameDto>? = null,
    @SerializedName("screenshots") val screenshots: List<IgdbImageDto>? = null
)

// Универсальный DTO для картинок (Обложки и Скриншоты)
data class IgdbImageDto(
    @SerializedName("image_id") val imageId: String?
)

// Универсальный DTO для Жанров и Платформ (у них структура одинаковая)
data class IgdbNameDto(
    @SerializedName("name") val name: String
)
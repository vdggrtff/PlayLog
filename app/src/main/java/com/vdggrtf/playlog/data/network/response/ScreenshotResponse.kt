package com.vdggrtf.playlog.data.network.response

import com.vdggrtf.playlog.data.network.dto.ScreenshotDto
import com.google.gson.annotations.SerializedName

data class ScreenshotResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("results") val result: List<ScreenshotDto>,
)

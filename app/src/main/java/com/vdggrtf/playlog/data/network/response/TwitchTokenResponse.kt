package com.vdggrtf.playlog.data.network.response

import com.google.gson.annotations.SerializedName

data class TwitchTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("token_type") val tokenType: String
)
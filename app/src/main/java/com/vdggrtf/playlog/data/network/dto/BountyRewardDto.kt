package com.vdggrtf.playlog.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BountyRewardDto(
    @SerialName("reward_points") val rewardPoints: Int
)
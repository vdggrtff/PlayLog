package com.vdggrtf.playlog.domain.model

import com.vdggrtf.playlog.R

enum class AchievementDifficulty(val title: String, val emoji: Int) {

    NONE("none", 0),
    EASY("easy", R.drawable.easy_difficult),
    MEDIUM("medium", R.drawable.medium_difficult),
    HARD("hard", R.drawable.hard_difficult),
    DEMON("demon", R.drawable.demon_difficult),
    IMPOSSIBLE("impossible", R.drawable.impossible_difficult),

    CUSTOM_CHALLENGE("custom_challenge", R.drawable.custom_challenge),
}
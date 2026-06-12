package com.vdggrtf.playlog.navigation

import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.navigation.Screen.Companion.ACHIEVE_HUNTING_SCREEN
import com.vdggrtf.playlog.navigation.Screen.Companion.LIBRARY_SCREEN
import com.vdggrtf.playlog.navigation.Screen.Companion.PROFILE_SCREEN
import com.vdggrtf.playlog.navigation.Screen.Companion.RECOMMENDATION_SCREEN

sealed class BottomBarItems(val route: String, val title: String, val icon: Int) {

    object Library :
        BottomBarItems(route = LIBRARY_SCREEN, title = "LIBRARY", icon = R.drawable.library_icon)

    object Home : BottomBarItems(
        route = RECOMMENDATION_SCREEN,
        title = "DISCOVERY",
        icon = R.drawable.search_icon
    )

    object Achievements : BottomBarItems(
        route = ACHIEVE_HUNTING_SCREEN,
        title = "TROPHIES",
        icon = R.drawable.achievement_icon
    )

    object Profile :
        BottomBarItems(route = PROFILE_SCREEN, title = "PROFILE", icon = R.drawable.profile_icon)
}
package com.vdggrtf.playlog.navigation

sealed class Screen(val route: String) {

    object SplashScreen : Screen(SPLASH_SCREEN)


    object RecommendationScreen : Screen(RECOMMENDATION_SCREEN)
    object SearchScreen : Screen(SEARCH_SCREEN)

    object AiRecommendationScreen : Screen(AI_RECOMMENDATION_SCREEN)

    object GameDetailsScreen : Screen(GAME_DETAILS_SCREEN)

    object LibraryScreen : Screen(LIBRARY_SCREEN)

    object ProfileScreen : Screen(PROFILE_SCREEN)

    object AchievementsHuntingScreen : Screen(ACHIEVE_HUNTING_SCREEN)

    object DifficultyGamesScreen : Screen(DIFFICULTY_GAMES_SCREEN)

    object LoginScreen : Screen(LOGIN_SCREEN)

    object RegistrationScreen : Screen(REGISTRATION_SCREEN)


    companion object {
        const val SPLASH_SCREEN = "splash_screen"

        const val RECOMMENDATION_SCREEN = "recommendation_screen"
        const val SEARCH_SCREEN = "search_screen"

        const val AI_RECOMMENDATION_SCREEN = "ai_recommendation_screen"

        const val GAME_DETAILS_SCREEN = "game_details_screen"

        const val LIBRARY_SCREEN = "library_screen"

        const val ACHIEVE_HUNTING_SCREEN = "achieve_hunting_screen"

        const val DIFFICULTY_GAMES_SCREEN = "difficulty_games_screen"

        const val PROFILE_SCREEN = "profile_screen"

        const val LOGIN_SCREEN = "login_screen"
        const val REGISTRATION_SCREEN = "registration_screen"

    }
}
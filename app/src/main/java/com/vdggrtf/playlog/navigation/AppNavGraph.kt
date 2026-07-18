package com.vdggrtf.playlog.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vdggrtf.playlog.presentation.auth.login.LoginRoute
import com.vdggrtf.playlog.presentation.auth.registrartion.RegistrationRoute
import com.vdggrtf.playlog.presentation.main.achieve_hunting_screen.AchievementsRoute
import com.vdggrtf.playlog.presentation.main.achieve_hunting_screen.difficulty_games_screen.DifficultyGamesRoute
import com.vdggrtf.playlog.presentation.main.game_details.GameDetailsRoute
import com.vdggrtf.playlog.presentation.main.my_library.LibraryRoute
import com.vdggrtf.playlog.presentation.main.profile.ProfileRoute
import com.vdggrtf.playlog.presentation.main.recommendation.RecommendationRoute
import com.vdggrtf.playlog.presentation.main.recommendation.ai.AiAssistantRoute
import com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges.ChallengeBoardRoute
import com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges.challenge.ChallengeDetailsRoute
import com.vdggrtf.playlog.presentation.main.recommendation.search.SearchRoute
import com.vdggrtf.playlog.presentation.splash.SplashRoute

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(250), initialOffsetX = { it }) + fadeIn(
                tween(
                    250
                )
            )
        },
        exitTransition = {
            slideOutHorizontally(animationSpec = tween(250), targetOffsetX = { -it }) + fadeOut(
                tween(250)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(250),
                initialOffsetX = { -it }) + fadeIn(tween(250))
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(250),
                targetOffsetX = { it }) + fadeOut(tween(250))
        }
    ) {
        composable(Screen.SplashScreen.route) {
            SplashRoute(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            })
        }
        composable(route = Screen.RegistrationScreen.route) {
            RegistrationRoute(
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                onNavigateToMain = {
                    navController.navigate(
                        Screen.LibraryScreen.route
                    )
                })
        }
        composable(route = Screen.LoginScreen.route) {
            LoginRoute(
                onNavigateToRegister = { navController.navigate(Screen.RegistrationScreen.route) },
                onNavigateToMain = {
                    navController.navigate(
                        Screen.LibraryScreen.route
                    )
                })
        }
        composable(route = Screen.RecommendationScreen.route) {
            RecommendationRoute(
                onSearchClick = { navController.navigate(Screen.SearchScreen.route) },
                onGameClick = { gameId -> navController.navigate("details/$gameId") },
                onAiAssistantClick = { navController.navigate(Screen.AiRecommendationScreen.route) },
                onNavigateToChallenges = { navController.navigate(Screen.CustomChallengesScreen.route) }
            )
        }
        composable(route = Screen.CustomChallengesScreen.route) {
            ChallengeBoardRoute(
                onBackClick = { navController.popBackStack() },
                onChallengeClick = { challengeId ->
                    navController.navigate("challenge_details/$challengeId")
                }
            )
        }
        composable(
            route = "challenge_details/{challengeId}",
            arguments = listOf(navArgument("challengeId") { type = NavType.IntType })
        ) { backStackEntry ->
            // FIX 2: Extract the ID from the URL and pass it to the Route
            val challengeId = backStackEntry.arguments?.getInt("challengeId") ?: return@composable

            ChallengeDetailsRoute(
                challengeId = challengeId, // Passed here!
                onBackClick = { navController.popBackStack() },
                onNavigateToGame = { gameId -> navController.navigate("details/$gameId") }
            )
        }
        composable(route = Screen.SearchScreen.route) {
            SearchRoute(
                onBack = { navController.popBackStack() },
                onGameClick = { gameId -> navController.navigate("details/$gameId") })
        }
        composable(
            route = "details/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) {
            GameDetailsRoute (onBackClick = { navController.popBackStack() })
        }
        composable(route = Screen.LibraryScreen.route) {
            LibraryRoute(
                onGameClick = { gameId ->
                    navController.navigate(
                        "details/$gameId"
                    )
                },
                onNavigateToSearch = { navController.navigate(Screen.SearchScreen.route) }
            )
        }
        composable(route = Screen.AchievementsHuntingScreen.route) {
            AchievementsRoute(onCategoryClick = { difficultyName ->
                navController.navigate("difficulty_games/$difficultyName")
            })
        }
        composable(
            route = "difficulty_games/{difficultyName}",
            arguments = listOf(navArgument("difficultyName") { NavType.StringType })
        ) { backStackEntry ->
            val diffName = backStackEntry.arguments?.getString("difficultyName") ?: "NONE"

            DifficultyGamesRoute(
                difficultyName = diffName,
                onBack = { navController.popBackStack() },
                onGameClick = { gameId ->
                    navController.navigate("details/$gameId")
                },
                onChallengeClick = { challengeId -> navController.navigate("challenge_details/$challengeId") }
            )
        }
        composable(Screen.ProfileScreen.route) {
            ProfileRoute(onLogoutSuccess = {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        composable(Screen.AiRecommendationScreen.route) {
            AiAssistantRoute(
                onBackClick = { navController.popBackStack() },
                onGameClick = { gameId ->
                    navController.navigate(
                        "details/$gameId"
                    )
                }
            )
        }

    }

}
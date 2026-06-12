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
import com.vdggrtf.playlog.presentation.auth.login.LoginScreen
import com.vdggrtf.playlog.presentation.auth.registrartion.RegistrationScreen
import com.vdggrtf.playlog.presentation.main.achieve_hunting_screen.AchievementsScreen
import com.vdggrtf.playlog.presentation.main.achieve_hunting_screen.difficulty_games_screen.DifficultyGamesScreen
import com.vdggrtf.playlog.presentation.main.game_details.GameDetailsScreen
import com.vdggrtf.playlog.presentation.main.my_library.LibraryScreen
import com.vdggrtf.playlog.presentation.main.profile.ProfileScreen
import com.vdggrtf.playlog.presentation.main.recommendation.RecommendationScreen
import com.vdggrtf.playlog.presentation.main.recommendation.ai.AiAssistantScreen
import com.vdggrtf.playlog.presentation.main.recommendation.search.SearchScreen
import com.vdggrtf.playlog.presentation.splash.SplashScreen

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
            SplashScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            })
        }
        composable(route = Screen.RegistrationScreen.route) {
            RegistrationScreen(
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                onNavigateToMain = {
                    navController.navigate(
                        Screen.LibraryScreen.route
                    )
                })
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.RegistrationScreen.route) },
                onNavigateToMain = {
                    navController.navigate(
                        Screen.LibraryScreen.route
                    )
                })
        }
        composable(route = Screen.RecommendationScreen.route) {
            RecommendationScreen(
                onSearchClick = { navController.navigate(Screen.SearchScreen.route) },
                onGameClick = { gameId -> navController.navigate("details/$gameId") },
                onAiAssistantClick = { navController.navigate(Screen.AiRecommendationScreen.route) })
        }
        composable(route = Screen.SearchScreen.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onGameClick = { gameId -> navController.navigate("details/$gameId") })
        }
        composable(
            route = "details/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) {
            GameDetailsScreen { navController.popBackStack() }
        }
        composable(route = Screen.LibraryScreen.route) {
            LibraryScreen(
                onGameClick = { gameId ->
                    navController.navigate(
                        "details/$gameId"
                    )
                },
                onNavigateToSearch = { navController.navigate(Screen.SearchScreen.route) }
            )
        }
        composable(route = Screen.AchievementsHuntingScreen.route) {
            AchievementsScreen(onCategoryClick = { difficultyName ->
                navController.navigate("difficulty_games/$difficultyName")
            })
        }
        composable(
            route = "difficulty_games/{difficultyName}",
            arguments = listOf(navArgument("difficultyName") { NavType.StringType })
        ) { backStackEntry ->
            val diffName = backStackEntry.arguments?.getString("difficultyName") ?: "NONE"

            DifficultyGamesScreen(
                difficultyName = diffName,
                onBack = { navController.popBackStack() },
                onGameClick = { gameId ->
                    navController.navigate("details/$gameId")
                }
            )
        }
        composable(Screen.ProfileScreen.route) {
            ProfileScreen(onLogoutSuccess = {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
        composable(Screen.AiRecommendationScreen.route) {
            AiAssistantScreen(onBackClick = { navController.popBackStack() }) { gameId ->
                navController.navigate(
                    "details/$gameId"
                )
            }
        }

    }

}
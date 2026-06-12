package com.vdggrtf.playlog.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vdggrtf.playlog.navigation.AppNavGraph
import com.vdggrtf.playlog.navigation.Screen
import com.vdggrtf.playlog.presentation.components.bottom_bar.CyberBottomBar
import com.vdggrtf.playlog.ui.theme.Background

@Composable
fun MainLayout() {

    val bottomNavController = rememberNavController()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBar =
        listOf(Screen.RegistrationScreen.route, Screen.LoginScreen.route, Screen.SplashScreen.route)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        bottomBar = {
            if (currentRoute !in hideBottomBar) {
                CyberBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        bottomNavController.navigate(targetRoute) {
                            // Standard behavior: clear backstack to start destination
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Background
        ) {
            AppNavGraph(bottomNavController)
        }

    }
}
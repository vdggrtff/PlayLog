package com.vdggrtf.playlog.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashRoute(
    onNavigate: (String) -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    val startDestination by viewModel.startDestination.collectAsState()

    LaunchedEffect(startDestination) {
        startDestination?.let {
            onNavigate(it)
        }
    }

    SplashScreen()
}

// 2. DUMB SCREEN: Только краски, холсты и анимации. Никакой логики!
@Composable
fun SplashScreen() {
    // Black screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
    )
}
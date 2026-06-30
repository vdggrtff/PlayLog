package com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus

// 1. SMART ROUTE (Handles ViewModel and Navigation)
@Composable
fun ChallengeBoardRoute(
    onBackClick: () -> Unit,
    onChallengeClick: (Int) -> Unit, // Passes Challenge ID
    showOnlyCompleted: Boolean = false,
    viewModel: ChallengeBoardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshChallenges()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filteredState = if (showOnlyCompleted) {
        state.copy(challenges = state.challenges.filter { it.isCompleted })
    } else {
        state
    }

    ChallengeBoardScreen(
        state = filteredState,
        onBackClick = onBackClick,
        onChallengeClick = onChallengeClick
    )
}

// 2. DUMB SCREEN (Pure UI, stateless)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeBoardScreen(
    state: ChallengeBoardState,
    onBackClick: () -> Unit,
    onChallengeClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bounty Board", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0F14))
            )
        },
        containerColor = Color(0xFF0F0F14)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF00E5FF))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.challenges) { challenge ->
                        BountyGridCard(
                            challenge = challenge,
                            onClick = { onChallengeClick(challenge.id) } // Simple click passes ID!
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BountyGridCard(
    challenge: CustomChallengeModel,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clip(cardShape)
            .background(Color(0xFF1E1E26)) // CardBackground
            .clickable { onClick() }
    ) {
        // 1. Постер мода/игры
        AsyncImage(
            model = challenge.imageUrl ?: "https://via.placeholder.com/300",
            contentDescription = challenge.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Градиент снизу для читаемости текста
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xDD0F0F14)),
                        startY = 300f
                    )
                )
        )

        if (!challenge.isCompleted && challenge.status != GameStatus.NONE) {
            val (badgeColor, badgeText) = if (challenge.status == GameStatus.PLAYING) {
                Color(0xFF00E5FF) to "PLAYING" // AiAccent
            } else {
                Color.Gray to "BACKLOG"
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart) // В левом верхнем углу!
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp
                )
            }
        }


        if (challenge.isCompleted) {
            // Затемняем карточку, чтобы она выглядела "закрытой"
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )

            // Диагональный неоновый штамп по центру
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer { rotationZ = -35f } // Поворачиваем штамп
                    .border(3.dp, Color(0xFF4CAF50).copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "CLAIMED", // Или "CLOSED"
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 4.sp
                )
            }
        }

        // 3. Бейджик XP (в правом верхнем углу)
        // Если контракт пройден - можно сделать бейджик серым, либо оставить как есть
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (challenge.isCompleted) Color.DarkGray.copy(alpha = 0.8f)
                    else Color(0xFFFF1744).copy(alpha = 0.8f)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${challenge.rewardPoints} XP",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }

        // 4. Текст с названием внизу
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = challenge.title,
                color = if (challenge.isCompleted) Color.Gray else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
package com.vdggrtf.playlog.presentation.main.game_details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.presentation.components.bottom_sheet.GameManagementBottomSheet
import com.vdggrtf.playlog.presentation.components.card.ExpandableDifficultySection
import com.vdggrtf.playlog.presentation.components.card_details.AchievementRow
import com.vdggrtf.playlog.presentation.components.card_details.CyberTabs
import com.vdggrtf.playlog.presentation.components.card_details.ExpandableDescription
import com.vdggrtf.playlog.presentation.components.card_details.GameHeaderSection
import com.vdggrtf.playlog.presentation.components.card_details.StoreLinksRow
import com.vdggrtf.playlog.presentation.components.carousel.BountiesCarousel
import com.vdggrtf.playlog.presentation.components.dialogs.AiVerificationDialogs
import com.vdggrtf.playlog.presentation.components.dialogs.ProofUploadDialog
import com.vdggrtf.playlog.presentation.components.dialogs.SelectPlaylistDialog
import com.vdggrtf.playlog.presentation.components.dialogs.UserRatingDialog
import com.vdggrtf.playlog.presentation.main.my_library.scaner.VerificationViewModel
import com.vdggrtf.playlog.ui.theme.Background
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun GameDetailsRoute(
    onBackClick: () -> Unit,
    gameViewModel: GameDetailsViewModel = hiltViewModel(),
    verificationViewModel: VerificationViewModel = hiltViewModel(),
) {
    val gameState by gameViewModel.state.collectAsState()
    val verificationState by verificationViewModel.state.collectAsState()
    var showProofDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }

    if (gameState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryPurple)
        }
        return
    }

    val game = gameState.game ?: return

    ProofUploadDialog(
        showDialog = showProofDialog,
        onDismiss = { showProofDialog = false },
        onImageReady = { imageBytes ->
            verificationViewModel.verifyAndCompleteGame(
                imageBytes = imageBytes,
                game = game,
                aiDifficulty = gameState.objectiveDifficulty
            )
        }
    )

    AiVerificationDialogs(
        isThinking = verificationState.isThinking,
        error = verificationState.error,
        onClearError = { verificationViewModel.clearError() },
        isSuccess = verificationState.isSuccess,
        successDialogContent = {
            // Передаем сюда именно диалог рейтинга, так как мы в деталях игры
            UserRatingDialog(
                aiDifficulty = gameState.objectiveDifficulty,
                onRate = { selectedDiff ->
                    gameViewModel.completeGameWithUserRating(selectedDiff)
                    verificationViewModel.resetSuccessState()
                },
                onSkip = {
                    gameViewModel.completeGameWithUserRating(AchievementDifficulty.NONE)
                    verificationViewModel.resetSuccessState()
                }
            )
        }
    )

    SelectPlaylistDialog(
        showDialog = showPlaylistDialog,
        playlists = gameState.myPlaylists,
        onDismiss = { showPlaylistDialog = false },
        onPlaylistSelected = { playlistId -> gameViewModel.addGameToPlaylist(playlistId) }
    )


    GameDetailsScreen(
        gameState = gameState,
        game = game,
        onBackClick = onBackClick,
        onProveClick = { showProofDialog = true }, // Команда открыть галерею
        onRetryAiClick = { gameViewModel.retryAiEvaluation() }, // Команда перепнуть ИИ
        onUpdateStatus = { newStatus -> gameViewModel.updateCurrentStatus(newStatus) },
        onAddToPlaylistClick = { showPlaylistDialog = true }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    gameState: GameDetailsState,
    game: GameModel,
    onBackClick: () -> Unit,
    onProveClick: () -> Unit,
    onRetryAiClick: () -> Unit,
    onUpdateStatus: (GameStatus) -> Unit,
    onAddToPlaylistClick: () -> Unit,
) {

    var selectedTab by remember { mutableIntStateOf(0) }
    var showBottomBar by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showBottomBar = true },
                containerColor = if (gameState.isSavedLibrary) Color(0xFF4CAF50) else PrimaryPurple,
                contentColor = Color.White
            ) {
                Icon(
                    if (gameState.isSavedLibrary) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (gameState.isSavedLibrary) stringResource(R.string.in_library) else stringResource(
                        R.string.add
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            //  HEADER
            item {
                Box { // We wrap Header and Back Button in a Box inside the list!
                    GameHeaderSection(gameState)

                    // FIX 3: Back button is now INSIDE the scrollable list.
                    // It will scroll up and disappear when user scrolls down to read text.
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp) // Offset for status bar
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    StoreLinksRow(
                        gameName = game.name,
                        cheapestPrice = gameState.cheapestPrice,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Ai block
                    ExpandableDifficultySection(
                        aiDifficulty = gameState.objectiveDifficulty,
                        userDifficulty = game.userDifficulty,
                        isAiThinking = gameState.isAiThinking,
                        isGameInLibrary = gameState.isSavedLibrary,
                        currentGameStatus = gameState.currentGameStatus,
                        onProveClick = onProveClick,
                        onRetryClick = onRetryAiClick,
                        communityDifficulty = gameState.communityDifficulty,
                        communityVotesCount = gameState.communityVotesCount
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            //  TABS
            item {
                CyberTabs(
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    achievementsCount = gameState.achievements.size
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // TAB content
            if (selectedTab == 0) {
                item {
                    ExpandableDescription(
                        text = game.descriptionRaw
                            ?: stringResource(R.string.description_is_out)
                    )
                }
            } else {
                if (gameState.achievements.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.no_achievements), color = Color.Gray)
                        }
                    }
                } else {
                    items(gameState.achievements) { ach -> AchievementRow(ach) }
                }
            }

            if (gameState.customChallenges.isNotEmpty()) {
                item {
                    BountiesCarousel(challenges = gameState.customChallenges)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Bottom sheet (Game walkthrough selection)
    if (showBottomBar) {
        GameManagementBottomSheet(
            gameState = gameState,
            onDismiss = { showBottomBar = false },
            onUpdateStatus = onUpdateStatus,
            onProveClick = onProveClick,
            onAddToPlaylistClick = onAddToPlaylistClick // 💥 ПРОКИДЫВАЕМ ДАЛЬШЕ
        )
    }
}

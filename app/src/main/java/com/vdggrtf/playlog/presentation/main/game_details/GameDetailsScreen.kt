package com.vdggrtf.playlog.presentation.main.game_details

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.presentation.components.card.ExpandableDifficultySection
import com.vdggrtf.playlog.presentation.components.card_details.AchievementRow
import com.vdggrtf.playlog.presentation.components.card_details.CyberTabs
import com.vdggrtf.playlog.presentation.components.card_details.ExpandableDescription
import com.vdggrtf.playlog.presentation.components.card_details.GameHeaderSection
import com.vdggrtf.playlog.presentation.components.card_details.StatusOptionRow
import com.vdggrtf.playlog.presentation.components.card_details.StoreLinksRow
import com.vdggrtf.playlog.presentation.components.dialogs.UserRatingDialog
import com.vdggrtf.playlog.presentation.main.my_library.scaner.VerificationViewModel
import com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges.BountyGridCard
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.Background
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun GameDetailsRoute(
    onBackClick: () -> Unit,
    gameViewModel: GameDetailsViewModel = hiltViewModel(),
    verificationViewModel: VerificationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val gameState by gameViewModel.state.collectAsState()
    val verificationState by verificationViewModel.state.collectAsState()


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

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val imageBytes = context.contentResolver.openInputStream(it)?.use { stream ->
                stream.readBytes()
            }
            if (imageBytes != null) {
                gameState.game?.let { currentGame ->
                    verificationViewModel.verifyAndCompleteGame(
                        imageBytes = imageBytes,
                        game = currentGame,
                        aiDifficulty = gameState.objectiveDifficulty
                    )
                }
            }
        }
    }

    // dialog ai error
    if (verificationState.error != null) {
        AlertDialog(
            onDismissRequest = { verificationViewModel.clearError() },
            title = { Text(stringResource(R.string.error_scanning)) },
            text = { Text(verificationState.error!!) },
            confirmButton = {
                TextButton(onClick = { verificationViewModel.clearError() }) {
                    Text(
                        stringResource(R.string.ok)
                    )
                }
            }
        )
    }

    // dialog ai thinking
    if (verificationState.isThinking) {
        AlertDialog(
            onDismissRequest = { /* Ai think don't close */ },
            containerColor = CardBackground,
            title = {
                Text(
                    stringResource(R.string.neural_scan_active),
                    color = AiAccent,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = PrimaryPurple)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stringResource(R.string.analyzing_pixels), color = Color.White)
                }
            },
            confirmButton = {}
        )
    }

    // dialog ai success
    if (verificationState.isSuccess) {
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

    GameDetailsScreen(
        gameState = gameState,
        game = game,
        onBackClick = onBackClick,
        onProveClick = { galleryLauncher.launch("image/*") }, // Команда открыть галерею
        onRetryAiClick = { gameViewModel.retryAiEvaluation() }, // Команда перепнуть ИИ
        onUpdateStatus = { newStatus -> gameViewModel.updateCurrentStatus(newStatus) }
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

            item {
                Spacer(modifier = Modifier.height(24.dp))

                if (gameState.customChallenges.isNotEmpty()) {
                    Text(
                        text = "ACTIVE BOUNTIES (${gameState.customChallenges.size})",
                        color = Color(0xFFFF9100), // Оранжевый цвет баунти
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Горизонтальный скролл (Карусель)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(gameState.customChallenges) { challenge ->
                            // Используем ту самую карточку, которую мы написали для Доски!
                            // Только задаем ей фиксированную ширину, чтобы она красиво скроллилась
                            Box(modifier = Modifier.width(200.dp)) {
                                BountyGridCard(
                                    challenge = challenge,
                                    onClick = {
                                        // TODO: В будущем можно открывать шторку деталей челленджа прямо тут
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Bottom sheet (Game walkthrough selection)
    if (showBottomBar) {
        ModalBottomSheet(
            onDismissRequest = { showBottomBar = false },
            containerColor = Color(0xFF1E1E26)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.game_management),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (gameState.currentGameStatus == GameStatus.COMPLETED) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                PrimaryPurple.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.game_completed_100_status_recorded),
                            color = PrimaryPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    StatusOptionRow(
                        stringResource(R.string.backlog_game_details),
                        gameState.currentGameStatus == GameStatus.BACKLOG
                    ) {
                        onUpdateStatus(GameStatus.BACKLOG)
                        showBottomBar = false
                    }
                    StatusOptionRow(
                        stringResource(R.string.playing_game_details),
                        gameState.currentGameStatus == GameStatus.PLAYING
                    ) {
                        onUpdateStatus(GameStatus.PLAYING)
                        showBottomBar = false
                    }

                    if (gameState.isSavedLibrary) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showBottomBar = false
                                onProveClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AiAccent.copy(
                                    alpha = 0.1f
                                )
                            ),
                            border = BorderStroke(1.dp, AiAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.confirm_100),
                                color = AiAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (gameState.isSavedLibrary) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onUpdateStatus(GameStatus.NONE)
                            showBottomBar = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF3B30).copy(
                                alpha = 0.1f
                            )
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFF3B30)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.remove_from_library),
                            color = Color(0xFFFF3B30),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

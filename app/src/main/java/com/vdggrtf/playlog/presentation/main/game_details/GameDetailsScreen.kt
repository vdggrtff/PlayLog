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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.presentation.components.card.ExpandableDifficultySection
import com.vdggrtf.playlog.presentation.components.card_details.AchievementRow
import com.vdggrtf.playlog.presentation.components.card_details.CyberTabs
import com.vdggrtf.playlog.presentation.components.card_details.ExpandableDescription
import com.vdggrtf.playlog.presentation.components.card_details.GameHeaderSection
import com.vdggrtf.playlog.presentation.components.card_details.StatusOptionRow
import com.vdggrtf.playlog.presentation.components.card_details.StoreLinksRow
import com.vdggrtf.playlog.presentation.dialogs.UserRatingDialog
import com.vdggrtf.playlog.presentation.main.my_library.scaner.VerificationViewModel
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.Background
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.PrimaryPurple


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: GameDetailsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showBottomBar by remember { mutableStateOf(false) }
    val verificationViewModel: VerificationViewModel = hiltViewModel()
    val verificationState by verificationViewModel.state.collectAsState()

    if (state.isLoading) {
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

    val game = state.game ?: return

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val imageBytes = context.contentResolver.openInputStream(it)?.use { stream ->
                stream.readBytes()
            }
            if (imageBytes != null) {
                state.game?.let { currentGame ->
                    verificationViewModel.verifyAndCompleteGame(
                        imageBytes = imageBytes,
                        game = currentGame,
                        aiDifficulty = state.objectiveDifficulty
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
            aiDifficulty = state.objectiveDifficulty,
            onRate = { selectedDiff ->
                viewModel.completeGameWithUserRating(selectedDiff)

                verificationViewModel.resetSuccessState()
            },
            onSkip = {
                viewModel.completeGameWithUserRating(AchievementDifficulty.NONE)
                verificationViewModel.resetSuccessState()
            }
        )
    }

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showBottomBar = true },
                containerColor = if (state.isSavedLibrary) Color(0xFF4CAF50) else PrimaryPurple,
                contentColor = Color.White
            ) {
                Icon(
                    if (state.isSavedLibrary) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isSavedLibrary) stringResource(R.string.in_library) else stringResource(
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
                        GameHeaderSection(state)

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
                            cheapestPrice = state.cheapestPrice,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Ai block
                        ExpandableDifficultySection(
                            aiDifficulty = state.objectiveDifficulty,
                            userDifficulty = game.userDifficulty,
                            isAiThinking = state.isAiThinking,
                            isGameInLibrary = state.isSavedLibrary,
                            currentGameStatus = state.currentGameStatus,
                            onProveClick = { galleryLauncher.launch("image/*") },
                            onRetryClick = { viewModel.retryAiEvaluation() },
                            communityDifficulty = state.communityDifficulty,
                            communityVotesCount = state.communityVotesCount
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                //  TABS
                item {
                    CyberTabs(
                        selectedTabIndex = selectedTab,
                        onTabSelected = { selectedTab = it },
                        achievementsCount = state.achievements.size
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
                    if (state.achievements.isEmpty()) {
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
                        items(state.achievements) { ach -> AchievementRow(ach) }
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

                if (state.currentGameStatus == GameStatus.COMPLETED) {
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
                        state.currentGameStatus == GameStatus.BACKLOG
                    ) {
                        viewModel.updateCurrentStatus(GameStatus.BACKLOG)
                        showBottomBar = false
                    }
                    StatusOptionRow(
                        stringResource(R.string.playing_game_details),
                        state.currentGameStatus == GameStatus.PLAYING
                    ) {
                        viewModel.updateCurrentStatus(GameStatus.PLAYING)
                        showBottomBar = false
                    }

                    if (state.isSavedLibrary) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showBottomBar = false
                                galleryLauncher.launch("image/*")
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

                if (state.isSavedLibrary) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.updateCurrentStatus(GameStatus.NONE)
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

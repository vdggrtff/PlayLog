package com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges.challenge

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import com.vdggrtf.playlog.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.presentation.components.card_details.StatusOptionRow
import com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges.ChallengeBoardViewModel

// 1. ROUTE (Smart Wrapper)
@Composable
fun ChallengeDetailsRoute(
    challengeId: Int,
    onBackClick: () -> Unit,
    onNavigateToGame: (Int) -> Unit,
    viewModel: ChallengeBoardViewModel = hiltViewModel() // Assuming you fetch the challenge here
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // We assume the ViewModel fetched the challenge based on NavArgument
    val challenge = state.challenges.find { it.id == challengeId } // Temporarily taking the first one for testing

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val imageBytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
            if (imageBytes != null && challenge != null) {
                viewModel.verifyChallengeProof(challenge, imageBytes)
            }
        }
    }

    // AI DIALOGS
    if (state.isVerifying) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color(0xFF1E1E26),
            title = { Text("NEURAL SCAN ACTIVE", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = Color(0xFF7C4DFF))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Analyzing pixels...", color = Color.White)
                }
            },
            confirmButton = {}
        )
    }

    if (state.successMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAlerts() },
            containerColor = Color(0xFF1E1E26),
            title = { Text("BOUNTY CLAIMED", color = Color(0xFFFF9100), fontWeight = FontWeight.Bold) },
            text = { Text(state.successMessage!!, color = Color.White) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAlerts() }) { Text("AWESOME", color = Color(0xFFFF9100)) }
            }
        )
    }

    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAlerts() },
            containerColor = Color(0xFF1E1E26),
            title = { Text("VERIFICATION FAILED", color = Color(0xFFFF1744), fontWeight = FontWeight.Bold) },
            text = { Text(state.error!!, color = Color.White) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAlerts() }) { Text("OK", color = Color(0xFFFF1744)) }
            }
        )
    }

    if (challenge == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F14)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF00E5FF))
        }
        return
    }

    ChallengeDetailsScreen(
        challenge = challenge,
        onBackClick = onBackClick,
        onNavigateToGame = onNavigateToGame,
        onAttachProofClick = { galleryLauncher.launch("image/*") },
        onUpdateStatus = { status -> viewModel.updateChallengeStatus(challenge.id, status) }
    )
}

// 2. SCREEN (Pure UI, Edge-to-Edge like GameDetailsScreen)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailsScreen(
    challenge: CustomChallengeModel,
    onBackClick: () -> Unit,
    onNavigateToGame: (Int) -> Unit,
    onAttachProofClick: () -> Unit,
    onUpdateStatus: (GameStatus) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    var showBottomBar by remember { mutableStateOf(false) }
    val isSavedLibrary = challenge.status != GameStatus.NONE

    Scaffold(
        containerColor = Color(0xFF0F0F14),
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showBottomBar = true },
                containerColor = if (isSavedLibrary) Color(0xFF4CAF50) else Color(0xFF00E5FF), // AiAccent instead of Purple for Bounties
                contentColor = Color.White
            ) {
                Icon(
                    if (isSavedLibrary) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSavedLibrary) stringResource(R.string.in_library) else stringResource(R.string.add),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 40.dp)
        ) {
            // HEADER & POSTER (Scrolls away with the content)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f) // Tall poster ratio
                ) {
                    AsyncImage(
                        model = challenge.imageUrl ?: "https://via.placeholder.com/600",
                        contentDescription = challenge.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim gradient for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF0F0F14)),
                                    startY = 500f
                                )
                            )
                    )

                    // Back Button (Inside the scrollable Box!)
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // XP Badge at bottom right of poster
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF1744).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFFF1744), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${challenge.rewardPoints} XP",
                            color = Color(0xFFFF1744),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // CONTENT SECTION
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = challenge.title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = challenge.description,
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // ACTION BUTTONS
                    if (challenge.isCompleted) {
                        // 🟢 STATE: ALREADY COMPLETED
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .background(Color(0xFF4CAF50).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("BOUNTY CLAIMED", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    } else {
                        // 🔵 STATE: READY TO VERIFY
                        Button(
                            onClick = onAttachProofClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ATTACH PROOF", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onNavigateToGame(challenge.gameId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E26)), // CardBackground
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("OPEN GAME CARD", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // DONATION BLOCK
                    val donateUrl = challenge.creatorDonateUrl ?: "https://boosty.to/"
                    val creatorName = challenge.creatorName ?: "Solo Dev (PlayLog)"

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Support the creator of this challenge",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { uriHandler.openUri(donateUrl) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100).copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, Color(0xFFFF9100)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("💖 Support $creatorName", color = Color(0xFFFF9100), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
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
                        text = stringResource(R.string.game_management), // "Manage Bounty"
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (challenge.status == GameStatus.COMPLETED) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF00E5FF).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                stringResource(R.string.game_completed_100_status_recorded),
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        StatusOptionRow(
                            stringResource(R.string.backlog_game_details),
                            challenge.status == GameStatus.BACKLOG
                        ) {
                            onUpdateStatus(GameStatus.BACKLOG)
                            showBottomBar = false
                        }
                        StatusOptionRow(
                            stringResource(R.string.playing_game_details),
                            challenge.status == GameStatus.PLAYING
                        ) {
                            onUpdateStatus(GameStatus.PLAYING)
                            showBottomBar = false
                        }

                        if (isSavedLibrary) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    showBottomBar = false
                                    onAttachProofClick() // <--- ЗАПУСКАЕТ ГАЛЕРЕЮ И ИИ!
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.1f)),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.confirm_100), color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (isSavedLibrary) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onUpdateStatus(GameStatus.NONE)
                                showBottomBar = false
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30).copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, Color(0xFFFF3B30)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.remove_from_library), color = Color(0xFFFF3B30), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

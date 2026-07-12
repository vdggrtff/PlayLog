package com.vdggrtf.playlog.presentation.main.recommendation.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.presentation.components.card.AiRecommendationCard
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.AiGradient
import com.vdggrtf.playlog.ui.theme.bgColor
import com.vdggrtf.playlog.ui.theme.cardColor

@Composable
fun AiAssistantRoute(
    onBackClick: () -> Unit,
    onGameClick: (String) -> Unit,
    viewModel: AiRecommendationGameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var promptText by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    AiAssistantScreen(
        state = state,
        promptText = promptText,
        onBackClick = onBackClick,
        onGameClick = onGameClick,
        onValueChange = { promptText = it},
        keyboardController = keyboardController,
        onAskAi = { viewModel.askAiForRecommendations(promptText)}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    state: AiGameState,
    promptText: String,
    keyboardController: SoftwareKeyboardController?,
    onAskAi: () -> Unit,
    onValueChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onGameClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(top = 40.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }
            Text(
                text = stringResource(R.string.ai_agent),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Content (or Loading, or Results)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = AiAccent)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.gemini_is_analyzing_the_game_database),
                            color = AiAccent,
                            fontSize = 14.sp
                        )
                    }
                }

                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error!!, color = Color(0xFFFF5252), fontSize = 16.sp)
                    }
                }

                state.recommendations.isEmpty() -> {
                    // Empty state (Instruction)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🤖", fontSize = 80.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.describe_your_dream_game),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.for_example_i_want_a_dark_shooter_in_space_with_a_great_plot_similar_to_dead_space),
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                else -> {
                    // Results
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.recommendations) { rec ->
                            AiRecommendationCard(
                                recommendation = rec,
                                onClick = {
                                    rec.gameDetails?.let { onGameClick(it.id.toString()) }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Chat
        Surface(
            color = cardColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptText,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.describe_the_game),
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AiAccent,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AiAccent
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(AiGradient, CircleShape)
                        .clickable(enabled = promptText.isNotBlank() && !state.isLoading) {
                            keyboardController?.hide()
                            onAskAi()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.send),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
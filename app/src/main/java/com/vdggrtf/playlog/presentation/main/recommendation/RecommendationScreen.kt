package com.vdggrtf.playlog.presentation.main.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.AiGradient
import com.vdggrtf.playlog.ui.theme.Background
import com.vdggrtf.playlog.ui.theme.CardBackground

@Composable
fun RecommendationScreen(
    onGameClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
) {
    val viewModel: RecommendationViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    val genreFilters = listOf("Action", "RPG", "Shooter", "Adventure", "Indie")
    var selectedGenre by remember { mutableStateOf("Action") }

    GamesListTemplate(
        title = "Recommendation",
        isLoading = state.isLoading,
        games = state.popularGames,

        // Genre
        filters = genreFilters,
        selectedFilter = selectedGenre,
        onFilterClick = { genre ->
            selectedGenre = genre
        },

        // Header With Ai
        headerContent = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

                // Search
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBackground)
                        .clickable { onSearchClick() }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.find_game), color = Color.Gray, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ai banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AiGradient)
                        .clickable { onAiAssistantClick() }
                        .padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Background)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                stringResource(R.string.ai_helper),
                                color = AiAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                stringResource(R.string.don_t_know_what_to_play),
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },

        emptyStateContent = {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.games_not_found), color = Color.Gray)
            }
        },

        onBack = null,
        onGameClick = onGameClick,
        onLoadMore = { viewModel.loadMoreGames() }
    )
}
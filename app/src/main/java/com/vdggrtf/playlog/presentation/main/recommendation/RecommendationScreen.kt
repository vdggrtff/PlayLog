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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate
import com.vdggrtf.playlog.presentation.components.tabs.DiscoveryWidgetsRow
import com.vdggrtf.playlog.ui.theme.CardBackground

@Composable
fun RecommendationRoute(
    onGameClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    viewModel: RecommendationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val genreFilters = listOf("Action", "RPG", "Shooter", "Adventure", "Indie")
    var selectedGenre by remember { mutableStateOf("Action") }


    RecommendationScreen(
        state = state,
        onGameClick = { onGameClick(it) },
        onSearchClick = onSearchClick,
        onAiAssistantClick = onAiAssistantClick,
        onNavigateToChallenges = onNavigateToChallenges,
        genreFilters = genreFilters,
        selectedGenre = selectedGenre,
        onFilterClick = { selectedGenre = it },
        onLoadMore = { viewModel.loadMoreGames() }

    )
}

@Composable
fun RecommendationScreen(
    state: RecommendationState,
    genreFilters: List<String>,
    selectedGenre: String,
    onLoadMore: () -> Unit,
    onFilterClick: (String) -> Unit,
    onGameClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onNavigateToChallenges: () -> Unit,
) {

    GamesListTemplate(
        title = "Recommendation",
        isLoading = state.isLoading,
        games = state.popularGames,

        // Genre
        filters = genreFilters,
        selectedFilter = selectedGenre,
        onFilterClick = onFilterClick,

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
                        Text(
                            stringResource(R.string.find_game),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ai banner
                DiscoveryWidgetsRow(
                    onAiHelperClick = { onAiAssistantClick() },
                    onChallengesClick = { onNavigateToChallenges() } // Переход на новый экран челленджей!
                )
            }
        },

        emptyStateContent = {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.games_not_found), color = Color.Gray)
            }
        },

        onBack = null,
        onGameClick = onGameClick,
        onLoadMore = onLoadMore
    )
}
package com.vdggrtf.playlog.presentation.main.recommendation

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.PlaylistModel
import com.vdggrtf.playlog.presentation.components.dashboard.DashboardSection
import com.vdggrtf.playlog.presentation.components.dashboard.GameCarouselCard
import com.vdggrtf.playlog.presentation.components.dashboard.PlaylistCarouselCard
import com.vdggrtf.playlog.presentation.components.dialogs.AdvancedFiltersScreen
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate
import com.vdggrtf.playlog.presentation.components.tabs.DiscoveryWidgetsRow
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import com.vdggrtf.playlog.ui.theme.Background
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
    val advancedFilters by viewModel.advancedFilters.collectAsState()


    RecommendationScreen(
        state = state,
        onGameClick = { onGameClick(it) },
        onSearchClick = onSearchClick,
        onAiAssistantClick = onAiAssistantClick,
        onNavigateToChallenges = onNavigateToChallenges,
        onLoadMore = { viewModel.loadMoreGames() },
        gridColumns = state.gridColumns,
        onToggleGrid = { viewModel.toggleGridColumns() },
        advancedFilters = advancedFilters,
        onApplyFilters = { newFilters -> viewModel.applyAdvancedFilters(newFilters) },
        onResetFilters = { viewModel.resetAdvancedFilters() },
        playlists = emptyList(),
        onSeeAllPopularClick = {},
        onPlaylistClick = {}
    )
}

@Composable
fun RecommendationScreen(
    state: RecommendationState,
    playlists: List<PlaylistModel>,
    advancedFilters: AdvancedFilters,
    onApplyFilters: (AdvancedFilters) -> Unit,
    onResetFilters: () -> Unit,
    gridColumns: Int,
    onToggleGrid: () -> Unit,
    onLoadMore: () -> Unit,
    onGameClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    onSeeAllPopularClick: () -> Unit,
    onPlaylistClick: (String) -> Unit,
    ) {
    /*var showFilterSheet by remember { mutableStateOf(false) }

    GamesListTemplate(
        title = "Recommendation",
        isLoading = state.isLoading,
        games = state.popularGames,
        gridColumns = gridColumns,
        onAdvancedFilterClick = { showFilterSheet = true },
        onToggleGridClick = onToggleGrid,
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
    if (showFilterSheet) {
        AdvancedFiltersScreen(
            currentFilters = advancedFilters,
            showDifficultyFilter = false,
            showBountiesToggle = false,
            onApply = { newFilters ->
                onApplyFilters(newFilters)
                showFilterSheet = false
            },
            onReset = {
                onResetFilters()
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }*/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background) // Твой Background
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()) // 💥 Весь экран скроллится!
            .padding(bottom = 100.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Search Bar
            Box(
                modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp))
                    .background(CardBackground).clickable { onSearchClick() }
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

            // AI Banner & Bounties
            DiscoveryWidgetsRow(
                onAiHelperClick = onAiAssistantClick,
                onChallengesClick = onNavigateToChallenges
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DashboardSection(
            title = "Community Playlists",
            onSeeAllClick = { /* TODO: Экран всех плейлистов */ }
        ) {
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No playlists yet", color = Color.Gray)
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistCarouselCard(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist.id) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        DashboardSection(
            title = "Popular Now",
            onSeeAllClick = onSeeAllPopularClick
        ) {
            if (state.isLoading && state.popularGames.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00E5FF))
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(state.popularGames) { game ->
                        GameCarouselCard(game = game, onClick = { onGameClick(game.id.toString()) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
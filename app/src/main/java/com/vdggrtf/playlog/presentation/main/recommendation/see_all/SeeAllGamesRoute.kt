package com.vdggrtf.playlog.presentation.main.recommendation.see_all

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.presentation.components.dialogs.AdvancedFiltersScreen
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate

@Composable
fun SeeAllGamesRoute(
    onBackClick: () -> Unit,
    onGameClick: (String) -> Unit,
    viewModel: SeeAllGamesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val advancedFilters by viewModel.advancedFilters.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    GamesListTemplate(
        title = state.title,
        isLoading = state.isLoading,
        games = state.games,
        gridColumns = state.gridColumns,

        onToggleGridClick = { viewModel.toggleGridColumns() },
        // Кнопку фильтров показываем ТОЛЬКО для популярных игр (Инди у нас захардкожены в UseCase)
        onAdvancedFilterClick = if (state.category == "popular") { { showFilterSheet = true } } else null,

        emptyStateContent = {
            if (!state.isLoading && state.error != null) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text(state.error!!, color = Color.Red) }
            }
        },

        onBack = onBackClick,
        onGameClick = onGameClick,
        onLoadMore = { viewModel.loadMore() }
    )

    // Шторка фильтров (только для Popular)
    if (showFilterSheet) {
        AdvancedFiltersScreen(
            currentFilters = advancedFilters,
            showDifficultyFilter = false,
            showBountiesToggle = false,
            onApply = { newFilters ->
                viewModel.applyAdvancedFilters(newFilters)
                showFilterSheet = false
            },
            onReset = {
                viewModel.resetAdvancedFilters()
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}
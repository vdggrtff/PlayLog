package com.vdggrtf.playlog.presentation.main.recommendation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.presentation.components.bottom_sheet.AdvancedFiltersScreen
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters

@Composable
fun SearchRoute(
    onBack: () -> Unit,
    onGameClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val advancedFilters by viewModel.advancedFilters.collectAsState()


    SearchScreen(
        state = state,
        gridColumns = state.gridColumns,
        onToggleGrid = { viewModel.toggleGridColumns() },
        advancedFilters = advancedFilters,
        onApplyFilters = { newFilters -> viewModel.applyAdvancedFilters(newFilters) },
        onResetFilters = { viewModel.resetAdvancedFilters() },
        onBack = onBack,
        onGameClick = onGameClick,
        onValueChange = { viewModel.onSearchQueryChange(it) },
        onLoadMore = { viewModel.loadMore() },
        onClear = { viewModel.onSearchQueryChange("") }
    )
}

@Composable
fun SearchScreen(
    state: SearchState,
    advancedFilters: AdvancedFilters,
    onApplyFilters: (AdvancedFilters) -> Unit,
    onResetFilters: () -> Unit,
    gridColumns: Int,
    onToggleGrid: () -> Unit,
    onBack: () -> Unit,
    onGameClick: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onLoadMore: () -> Unit,
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    GamesListTemplate(
        title = "Search",
        isLoading = state.isLoading,
        games = state.searchResult,
        gridColumns = gridColumns,
        onAdvancedFilterClick = { showFilterSheet = true },
        onToggleGridClick = onToggleGrid,
        // Header
        headerContent = {
            OutlinedTextField(
                value = state.query,
                onValueChange = onValueChange,
                placeholder = { Text(stringResource(R.string.add_game_name), color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear),
                                tint = Color.White
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6200EA),
                    unfocusedBorderColor = Color(0xFF1E1E26),
                    focusedContainerColor = Color(0xFF1E1E26),
                    unfocusedContainerColor = Color(0xFF1E1E26)
                )
            )
        },

        // if there are no games (empty state)
        emptyStateContent = {
            val message =
                if (state.query.isEmpty()) stringResource(R.string.let_s_find_some_games) else stringResource(
                    R.string.game_not_found
                )
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(message, color = Color.Gray, fontSize = 16.sp)
            }
        },

        // infinite scroll
        onLoadMore = onLoadMore,
        onBack = onBack,
        onGameClick = onGameClick
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
    }

}
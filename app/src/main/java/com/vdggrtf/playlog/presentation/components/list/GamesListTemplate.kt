package com.vdggrtf.playlog.presentation.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.presentation.components.card.GameGridCard
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.accentColor
import com.vdggrtf.playlog.ui.theme.bgColor

@Composable
fun GamesListTemplate(
    title: String,
    isLoading: Boolean,
    games: List<GameModel>,
    headerContent: @Composable (() -> Unit)? = null,
    filters: List<String> = emptyList(),
    selectedFilter: String = "",
    onFilterClick: (String) -> Unit = {},
    emptyStateContent: @Composable () -> Unit,
    onBack: (() -> Unit)? = null,
    onGameClick: (String) -> Unit,
    onLoadMore: () -> Unit = {}
) {
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= totalItems - 4 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoading) onLoadMore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
    ) {
        //  Header (print if have name or button back)
        if (title.isNotEmpty() || onBack != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                }
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (headerContent != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    headerContent()
                }
            }

            if (filters.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filters) { filterName ->
                            FilterChip(
                                selected = selectedFilter == filterName,
                                onClick = { onFilterClick(filterName) },
                                label = { Text(filterName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor,
                                    containerColor = CardBackground,
                                    labelColor = Color.Gray,
                                    selectedLabelColor = Color.White
                                ),
                                border = null
                            )
                        }
                    }
                }
            }

            if (games.isEmpty() && !isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    emptyStateContent()
                }
            } else {
                items(games) { game ->
                    GameGridCard(game = game, onClick = { onGameClick(game.id.toString()) })
                }
            }

            if (isLoading && games.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                }
            }
        }
    }
}

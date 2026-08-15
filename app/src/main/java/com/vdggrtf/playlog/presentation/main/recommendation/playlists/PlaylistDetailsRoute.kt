package com.vdggrtf.playlog.presentation.main.recommendation.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate

@Composable
fun PlaylistDetailsRoute(
    onBackClick: () -> Unit,
    onGameClick: (String) -> Unit,
    viewModel: PlaylistDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var gridColumns by remember { mutableIntStateOf(2) }
    var selectedGameId by remember { mutableStateOf<Int?>(null) }

    GamesListTemplate(
        title = state.playlistTitle,
        isLoading = state.isLoading,
        games = state.games,
        gridColumns = gridColumns,
        onToggleGridClick = {
            gridColumns = when (gridColumns) { 1 -> 2; 2 -> 4; 4 -> 1; else -> 2 }
        },
        emptyStateContent = {
            Box(Modifier.fillMaxSize().padding(top = 100.dp), Alignment.Center) {
                Text("This playlist is empty. Add games to it!", color = Color.Gray, fontSize = 16.sp)
            }
        },
        onBack = onBackClick,
        onGameClick = { gameIdStr ->
            // 💥 Вместо прямого перехода, открываем диалог выбора действий!
            selectedGameId = gameIdStr.toIntOrNull()
        }
    )

    if (selectedGameId != null) {
        AlertDialog(
            onDismissRequest = { selectedGameId = null },
            containerColor = Color(0xFF1E1E26),
            title = { Text("Game Options", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("What do you want to do with this game?", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = {
                        onGameClick(selectedGameId.toString()) // Переходим в карточку!
                        selectedGameId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) { Text("OPEN CARD", color = Color.Black, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.removeGame(selectedGameId!!) // Удаляем!
                        selectedGameId = null
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF1744))
                ) { Text("REMOVE FROM PLAYLIST") }
            }
        )
    }
}
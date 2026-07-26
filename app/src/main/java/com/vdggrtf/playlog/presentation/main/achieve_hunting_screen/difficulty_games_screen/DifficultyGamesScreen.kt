package com.vdggrtf.playlog.presentation.main.achieve_hunting_screen.difficulty_games_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate
import com.vdggrtf.playlog.presentation.main.my_library.LibraryState
import com.vdggrtf.playlog.presentation.main.my_library.MyLibraryViewModel
import com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges.ChallengeBoardRoute

@Composable
fun DifficultyGamesRoute(
    difficultyName: String,
    onBack: () -> Unit,
    onGameClick: (String) -> Unit,
    onChallengeClick: (Int) -> Unit,
    viewModel: MyLibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (difficultyName == AchievementDifficulty.CUSTOM_CHALLENGE.name) {
        // 🚀 If Challenge go to Challenge board
        ChallengeBoardRoute(
            onBackClick = onBack,
            onChallengeClick = onChallengeClick,
            showOnlyCompleted = true // <-- only Completed challenges
        )
        return // <-- Exit the composable
    }

    // Converting string from navigation back into an Enum.
    val difficulty = try {
        AchievementDifficulty.valueOf(difficultyName)
    } catch (e: Exception) {
        AchievementDifficulty.NONE
    }

    // Filter games by difficulty
    // We cache the filtered list to prevent heavy recalculations on every UI frame.
    // It will only recalculate if 'state.games' or 'difficulty' changes.
    val filteredGames = remember(state.games, difficulty) {
        state.games.filter { it.verifiedDifficulty == difficulty }
    }

    DifficultyGamesScreen(
        state = state,
        onBack = onBack,
        onGameClick = onGameClick,
        difficulty = difficulty,
        filteredGames = filteredGames
    )
}

@Composable
fun DifficultyGamesScreen(
    state: LibraryState,
    difficulty: AchievementDifficulty,
    filteredGames: List<GameModel>,
    onBack: () -> Unit,
    onGameClick: (String) -> Unit,
) {

    // Screen
    GamesListTemplate(
        title = difficulty.title.uppercase(),
        isLoading = state.isLoading,
        games = filteredGames,
        onBack = onBack,
        onGameClick = onGameClick,
        emptyStateContent = {
            // Just in case
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    stringResource(R.string.there_are_no_completed_games_in_this_category_yet),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    )
}
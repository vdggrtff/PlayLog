package com.vdggrtf.playlog.presentation.main.achieve_hunting_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.presentation.components.card.DifficultySquareCard
import com.vdggrtf.playlog.presentation.main.my_library.LibraryState
import com.vdggrtf.playlog.presentation.main.my_library.MyLibraryViewModel

data class PyramidState(
    val row1: List<AchievementDifficulty>,
    val row2: List<AchievementDifficulty>,
    val row3: List<AchievementDifficulty>,
    val gamesByDifficulty: Map<AchievementDifficulty, List<GameModel>>,
)

@Composable
fun AchievementsRoute(
    onCategoryClick: (String) -> Unit,
    viewModel: MyLibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // We use 'remember' to cache this heavy calculation.
    // It will only re-run if the 'state.games' list actually changes, saving battery and CPU.
    val pyramidState = remember(state.games) {

        // 1. Filter out games that haven't been rated/verified yet
        val completedGames = state.games.filter { it.verifiedDifficulty != AchievementDifficulty.NONE }

        // 2. Group the remaining games by their difficulty
        // This creates a Map<Difficulty, List<Game>> for incredibly fast O(1) lookups in the UI
        val grouped = completedGames.groupBy { it.verifiedDifficulty }

        // 3. Get all available difficulty levels from the Enum (excluding the empty NONE state)
        val allDiffs = AchievementDifficulty.entries.filter { it != AchievementDifficulty.NONE }

        // 4. Slice the Enum list into 3 rows to build a visual pyramid (3 -> 2 -> 1)
        val r1 = allDiffs.take(3)          // Top row: Takes the first 3 items (EASY, MEDIUM, HARD)
        val r2 = allDiffs.drop(3).take(2)  // Middle row: Skips the first 3, takes the next 2 (DEMON, IMPOSSIBLE)
        val r3 = allDiffs.drop(5)          // Bottom row: Skips the first 5, takes the rest (CUSTOM_CHALLENGE)

        PyramidState(
            row1 = r1,
            row2 = r2,
            row3 = r3,
            gamesByDifficulty = grouped
        )
    }

    AchievementsScreen(
        state = state,
        row1 = pyramidState.row1,
        row2 = pyramidState.row2,
        row3 = pyramidState.row3,
        gamesByDifficulty = pyramidState.gamesByDifficulty,
        onCategoryClick = onCategoryClick,
    )
}

@Composable
fun AchievementsScreen(
    state: LibraryState,
    row1: List<AchievementDifficulty>,
    row2: List<AchievementDifficulty>,
    row3: List<AchievementDifficulty>,
    gamesByDifficulty: Map<AchievementDifficulty, List<GameModel>>,
    onCategoryClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
            .padding(16.dp)
            .padding(top = 40.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            stringResource(R.string.hall_of_fame),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            stringResource(R.string.your_completed_games),
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Top Row (3 card)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            row1.forEach { difficulty ->
                DifficultySquareCard(
                    difficulty = difficulty,
                    count = gamesByDifficulty[difficulty]?.size ?: 0,
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick(difficulty.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // medium row (2 card)
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(0.5f))
            DifficultySquareCard(
                difficulty = row2[0],
                count = gamesByDifficulty[row2[0]]?.size ?: 0,
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick(row2[0].name) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            DifficultySquareCard(
                difficulty = row2[1],
                count = gamesByDifficulty[row2[1]]?.size ?: 0,
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick(row2[1].name) }
            )
            Spacer(modifier = Modifier.weight(0.5f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // bottom row (1 card - custom challenge)
        if (row3.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                DifficultySquareCard(
                    difficulty = row3[0],
                    // FIXED: Replaced standard local games count with the actual completed bounties count from Supabase
                    count = state.completedBountiesCount,
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick(row3[0].name) }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}


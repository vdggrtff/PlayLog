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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.presentation.components.card.DifficultySquareCard
import com.vdggrtf.playlog.presentation.main.my_library.MyLibraryViewModel

@Composable
fun AchievementsScreen(
    onCategoryClick: (String) -> Unit,
) {
    val viewModel: MyLibraryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    val completedGames = state.games.filter { it.verifiedDifficulty != AchievementDifficulty.NONE }
    val gamesByDifficulty = completedGames.groupBy { it.verifiedDifficulty }
    val allDifficulties = AchievementDifficulty.entries.filter { it != AchievementDifficulty.NONE }

    // breaking it down into 3 levels for the perfect pyramid (3 -> 2 -> 1).
    val row1 = allDifficulties.take(3)          // EASY, MEDIUM, HARD
    val row2 = allDifficulties.drop(3).take(2)  // DEMON, IMPOSSIBLE
    val row3 = allDifficulties.drop(5)          // CUSTOM_CHALLENGE


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
                    count = gamesByDifficulty[row3[0]]?.size ?: 0,
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick(row3[0].name) }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}


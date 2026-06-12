package com.vdggrtf.playlog.presentation.components.mylibrary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun LibraryHeader(
    allGames: List<GameModel>,
    selectedStatus: GameStatus,
    onStatusSelected: (GameStatus) -> Unit,
) {
    val totalGames = allGames.size
    val backlogCount = allGames.count { it.status == GameStatus.BACKLOG }
    val playingCount = allGames.count { it.status == GameStatus.PLAYING }
    val completedCount = allGames.count { it.status == GameStatus.COMPLETED }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // segmented progress bar
        if (totalGames > 0) {
            Text(
                text = stringResource(R.string.total_games, totalGames),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Backlog
                if (backlogCount > 0) {
                    Box(
                        modifier = Modifier
                            .weight(backlogCount.toFloat())
                            .fillMaxHeight()
                            .background(Color.DarkGray)
                    )
                }
                // Playing
                if (playingCount > 0) {
                    Box(
                        modifier = Modifier
                            .weight(playingCount.toFloat())
                            .fillMaxHeight()
                            .background(AiAccent)
                    )
                }
                // Completed
                if (completedCount > 0) {
                    Box(
                        modifier = Modifier
                            .weight(completedCount.toFloat())
                            .fillMaxHeight()
                            .background(PrimaryPurple)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // minimalistic tabs
        val statuses = listOf(GameStatus.BACKLOG, GameStatus.PLAYING, GameStatus.COMPLETED)

        Row(modifier = Modifier.fillMaxWidth()) {
            statuses.forEach { status ->
                val isSelected = selectedStatus == status

                val indicatorColor = when (status) {
                    GameStatus.BACKLOG -> Color.LightGray
                    GameStatus.PLAYING -> AiAccent
                    GameStatus.COMPLETED -> PrimaryPurple
                    else -> Color.Transparent
                }

                val title = when (status) {
                    GameStatus.BACKLOG -> stringResource(R.string.backlog)
                    GameStatus.PLAYING -> stringResource(R.string.playing)
                    GameStatus.COMPLETED -> stringResource(R.string.completed)
                    else -> ""
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStatusSelected(status) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // The strip under the active tab
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (isSelected) indicatorColor else Color(0xFF1E1E26))
                    )
                }
            }
        }
    }
}
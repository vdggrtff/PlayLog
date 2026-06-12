package com.vdggrtf.playlog.presentation.components.card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun ExpandableDifficultySection(
    aiDifficulty: AchievementDifficulty,
    userDifficulty: AchievementDifficulty,
    communityDifficulty: AchievementDifficulty,
    isAiThinking: Boolean,
    isGameInLibrary: Boolean,
    currentGameStatus: GameStatus,
    communityVotesCount: Int,
    onProveClick: () -> Unit,
    onRetryClick: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize() // Smooth expand animation
            .background(CardBackground, RoundedCornerShape(12.dp))
            .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // HEADER (AI RATING & VERIFY BUTTON)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    stringResource(R.string.ai_analysis),
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (isAiThinking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AiAccent,
                        strokeWidth = 2.dp
                    )
                } else if (aiDifficulty == AchievementDifficulty.NONE) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.failed),
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onRetryClick, modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF0F0F14), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.retry),
                                tint = AiAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = aiDifficulty.emoji),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            aiDifficulty.title.uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // SHOW VERIFY BUTTON ONLY IF NOT COMPLETED YET
            if (isGameInLibrary && currentGameStatus != GameStatus.COMPLETED) {
                Button(
                    onClick = onProveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AiAccent.copy(alpha = 0.15f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AiAccent)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = AiAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.verify), color = AiAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // EXPANDED CONTENT (USER & COMMUNITY)
        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            // USER RATING
            DifficultyInfoRow(title = stringResource(R.string.your_rating), difficulty = userDifficulty)

            Spacer(modifier = Modifier.height(12.dp))

            // COMMUNITY RATING (For now, we mock it. Later we fetch it from Supabase)
            DifficultyInfoRow(
                title = stringResource(R.string.community_rating),
                // Теперь мы передаем сюда РЕАЛЬНУЮ оценку из стейта!
                difficulty = communityDifficulty,
                subtitle = if (communityVotesCount > 0) stringResource(
                    R.string.based_on_votes,
                    communityVotesCount
                ) else stringResource(
                    R.string.no_votes_yet
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // EXPAND TOGGLE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isExpanded) stringResource(R.string.hide_ratings) else stringResource(R.string.show_all_ratings),
                color = PrimaryPurple,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Helper Row for clean UI
@Composable
fun DifficultyInfoRow(title: String, difficulty: AchievementDifficulty, subtitle: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                title,
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    color = Color.DarkGray,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (difficulty == AchievementDifficulty.NONE) {
            Text(stringResource(R.string.n_a), color = Color.DarkGray, fontWeight = FontWeight.Black, fontSize = 14.sp)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    difficulty.title.uppercase(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = difficulty.emoji),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
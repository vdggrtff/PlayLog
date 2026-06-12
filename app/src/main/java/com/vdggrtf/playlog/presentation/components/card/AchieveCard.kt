package com.vdggrtf.playlog.presentation.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty

@Composable
fun DifficultySquareCard(
    difficulty: AchievementDifficulty,
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isUnlocked = count > 0

    val bgColor = if (isUnlocked) Color(0xFF1E1E26) else Color(0xFF15151C)
    val borderColor = if (isUnlocked) Color(0xFF6200EA) else Color.DarkGray
    val countColor = if (isUnlocked) Color(0xFF00E5FF) else Color.Gray

    Column(
        modifier = modifier
            .aspectRatio(0.85f)
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(if (isUnlocked) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .alpha(if (isUnlocked) 1f else 0.4f), // Эффект неактивности
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = difficulty.emoji),
            contentDescription = difficulty.title,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = difficulty.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.achievement_counter_games, count),
            color = countColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
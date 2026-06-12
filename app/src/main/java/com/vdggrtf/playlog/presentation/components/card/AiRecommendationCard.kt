package com.vdggrtf.playlog.presentation.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.presentation.main.recommendation.ai.AiRecommendedGame

@Composable
fun AiRecommendationCard(
    recommendation: AiRecommendedGame,
    onClick: () -> Unit
) {
    val game = recommendation.gameDetails

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E26), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), RoundedCornerShape(16.dp)) // Неоновая обводка
            .clickable(enabled = game != null) { onClick() }
            .padding(16.dp)
    ) {
        // if rawg find game - show image
        if (game != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = game.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "⭐ ${game.rating ?: stringResource(R.string.n_a)}", color = Color(0xFFFFC107), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Ai
        Row(verticalAlignment = Alignment.Top) {
            Text("✨", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = recommendation.aiReason,
                color = Color.LightGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
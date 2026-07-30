package com.vdggrtf.playlog.presentation.components.card_details

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.data.network.dto.rawg.AchievementDto

@Composable
fun AchievementRow(achievement: AchievementDto) {
    val achName = achievement.name ?: stringResource(R.string.secret_achievement)
    val achDesc = achievement.description ?: stringResource(R.string.description_lost)
    val achPercent = achievement.percent ?: 0.0

    var isUnlocked by remember { mutableStateOf(false) }

    val colorMatrix = ColorMatrix()
    if (!isUnlocked) {
        colorMatrix.setToSaturation(0f)
    }

    val grayscaleFilter = remember { ColorFilter.colorMatrix(colorMatrix) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFF1E1E26), RoundedCornerShape(12.dp))
            .clickable { isUnlocked = !isUnlocked }
            .padding(12.dp)
            .alpha(if (isUnlocked) 1f else 0.5f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = achievement.image ?: "",
            contentDescription = achName,
            contentScale = ContentScale.Crop,
            colorFilter = if (!isUnlocked) grayscaleFilter else null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Name and description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achDesc,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // percent difficulty
        val percentColor = when {
            achPercent < 5.0 -> Color(0xFFFF5252)
            achPercent < 20.0 -> Color(0xFFFFC107)
            else -> Color(0xFF4CAF50)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format("%.1f%%", achPercent),
                color = if (isUnlocked) percentColor else Color.Gray,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            Text(
                text = stringResource(R.string.gamers),
                color = Color.DarkGray,
                fontSize = 10.sp
            )
        }
    }
}
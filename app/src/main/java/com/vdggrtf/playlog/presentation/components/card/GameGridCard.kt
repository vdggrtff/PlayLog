package com.vdggrtf.playlog.presentation.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.cardColor

@Composable
fun GameGridCard(
    game: GameModel,
    gridColumns: Int = 2,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // ==========================================
            // ВЕРХНЯЯ ЧАСТЬ: ПОСТЕР И БЕЙДЖИ
            // ==========================================
            Box {
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = game.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)
                        .background(Color.DarkGray)
                )

                // 💥 1. КРУЖОК РЕЙТИНГА ОТ СТИЧА (Слева вверху)
                val score = (game.rating ?: 0.0).toInt()
                if (score > 0) {
                    val scoreColor = when {
                        score >= 80 -> AiAccent // Голубой шедевр
                        score >= 50 -> Color(0xFFFF9800) // Оранжевый середняк
                        else -> Color(0xFFFF1744) // Красный мусор
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(34.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = score.toString(),
                            color = scoreColor, // Цифра будет светиться цветом оценки!
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                // 💥 2. ТВОИ ИКОНКИ СЛОЖНОСТИ (Справа вверху)
                val activeDifficulty = if (game.verifiedDifficulty != AchievementDifficulty.NONE) {
                    game.verifiedDifficulty
                } else if (game.aiDifficulty != AchievementDifficulty.NONE) {
                    game.aiDifficulty
                } else null

                if (activeDifficulty != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = activeDifficulty.emoji),
                            contentDescription = activeDifficulty.title,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ==========================================
            // НИЖНЯЯ ЧАСТЬ: ИМЯ И КИБЕР-ДАТА
            // ==========================================
            if (gridColumns < 4) {
                Column(modifier = Modifier.padding(12.dp)) {

                    // 💥 3. КИБЕРПАНК ПРЕФИКС ГОДА (SYS_YR)
                    val year = game.releasedDate?.take(4) ?: "UNKNOWN"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(AiAccent, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SYS_YR: $year",
                                color = AiAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        // Если есть часы прохождения - аккуратно выводим их справа
                        if (game.playtime > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${game.playtime}h",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Название игры (Сделал maxLines = 2, так как мы освободили место!)
                    Text(
                        text = game.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
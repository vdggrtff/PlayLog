package com.vdggrtf.playlog.presentation.components.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.Background
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun GamerPassportUi(
    nickname: String,
    totalGames: Int,
    completedGames: Int,
    favDifficulty: String,
    customChallengesCount: Int,
    totalBounty: Int
) {
    // vertical ID-card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.height(420.dp)
            .clip(CutCornerShape(topStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(colors = listOf(CardBackground, Color(0xFF050508))))
            .border(
                1.dp,
                Brush.linearGradient(listOf(PrimaryPurple, AiAccent)),
                CutCornerShape(topStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(24.dp)
    ) {
        // App logo
        Image(
            painter = painterResource(id = R.drawable.pl_logo),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .alpha(0.05f)
                .rotate(-15f)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        stringResource(R.string.sys_user_id),
                        color = AiAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        nickname.uppercase(),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.Green, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.authenticated),
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                        .background(Background)
                        .border(1.dp, AiAccent, CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Statistic
            CyberStatItem(
                stringResource(R.string.games_in_library),
                totalGames.toString(),
                PrimaryPurple
            )
            Spacer(modifier = Modifier.height(12.dp))
            CyberStatItem(
                stringResource(R.string._100_completed),
                completedGames.toString(),
                AiAccent
            )
            Spacer(modifier = Modifier.height(12.dp))
            CyberStatItem(
                stringResource(R.string.peak_difficulty),
                favDifficulty.uppercase(),
                Color(0xFFFF3B30)
            )
            Spacer(modifier = Modifier.height(12.dp))
            CyberStatItem(
                stringResource(R.string.mythical_trophies),
                customChallengesCount.toString(),
                Color(0xFFFFD700)
            )
            Spacer(modifier = Modifier.height(12.dp))
            CyberStatItem(
                label = "TOTAL BOUNTY", // Can move to strings.xml later
                value = "$totalBounty", // Added Cyber-Credits symbol
                color = Color(0xFFFF9100) // Badass Orange/Gold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        stringResource(R.string.verified_by_playlog_ai),
                        color = PrimaryPurple.copy(alpha = 0.6f),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.timestamp, System.currentTimeMillis() / 10000),
                        color = Color.DarkGray,
                        fontSize = 7.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CyberStatItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .border(0.5.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // left text
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // right text
        Text(
            text = value,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
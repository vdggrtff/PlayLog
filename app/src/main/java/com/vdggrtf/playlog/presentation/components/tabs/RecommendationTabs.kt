package com.vdggrtf.playlog.presentation.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.AiGradient
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.Pink80
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun DiscoveryWidgetsRow(
    onAiHelperClick: () -> Unit,
    onChallengesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. AI Helper Widget (Using your AiGradient and AiAccent)
        CyberWidget(
            modifier = Modifier.weight(1f),
            title = "AI Helper",
            subtitle = "Find a game",
            icon = Icons.Default.AutoAwesome,
            borderBrush = AiGradient,
            iconTint = AiAccent,
            onClick = onAiHelperClick
        )

        // 2. Custom Challenges / Bounties Widget
        // Mixing your Pink and Purple for a hot bounty vibe
        val bountyGradient = remember {
            Brush.horizontalGradient(
                colors = listOf(Pink80, PrimaryPurple)
            )
        }

        CyberWidget(
            modifier = Modifier.weight(1f),
            title = "Bounties",
            subtitle = "Custom tasks",
            icon = Icons.Default.LocalFireDepartment,
            borderBrush = bountyGradient,
            iconTint = Pink80,
            onClick = onChallengesClick
        )
    }
}

@Composable
private fun CyberWidget(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    borderBrush: Brush,
    iconTint: Color,
    onClick: () -> Unit
) {
    // We can change this to CutCornerShape later if we want a more aggressive Cyberpunk look
    val widgetShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(86.dp)
            .clip(widgetShape)
            .background(CardBackground) // Using your CardBackground color
            .border(
                width = 1.5.dp,
                brush = borderBrush,
                shape = widgetShape
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}
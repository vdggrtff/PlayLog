package com.vdggrtf.playlog.presentation.components.card_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun CyberTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    achievementsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // TAB 1 ABOUT GAME
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CutCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .background(if (selectedTabIndex == 0) PrimaryPurple else CardBackground)
                .clickable { onTabSelected(0) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ABOUT GAME",
                color = if (selectedTabIndex == 0) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.width(2.dp))

        // TAB 2 ACHIEVEMENTS
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(CutCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .background(if (selectedTabIndex == 1) PrimaryPurple else CardBackground)
                .clickable { onTabSelected(1) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ACHIEVEMENTS ($achievementsCount)",
                color = if (selectedTabIndex == 1) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
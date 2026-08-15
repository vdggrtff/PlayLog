package com.vdggrtf.playlog.presentation.components.carousel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges.BountyGridCard

@Composable
fun BountiesCarousel(
    challenges: List<CustomChallengeModel>,
    modifier: Modifier = Modifier
) {
    if (challenges.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "ACTIVE BOUNTIES (${challenges.size})",
            color = Color(0xFFFF9100),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Горизонтальный скролл (Карусель)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp) // Паддинги по краям
        ) {
            items(challenges) { challenge ->
                Box(modifier = Modifier.width(200.dp)) {
                    BountyGridCard(
                        challenge = challenge,
                        onClick = { /* TODO в будущем */ }
                    )
                }
            }
        }
    }
}
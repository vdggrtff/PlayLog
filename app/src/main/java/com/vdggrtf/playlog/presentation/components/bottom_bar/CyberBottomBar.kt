package com.vdggrtf.playlog.presentation.components.bottom_bar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.navigation.BottomBarItems
import com.vdggrtf.playlog.ui.theme.Background

@Composable
fun CyberBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomBarItems.Library,
        BottomBarItems.Home,
        BottomBarItems.Achievements,
        BottomBarItems.Profile
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background.copy(alpha = 0.98f))
            .navigationBarsPadding()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            CyberBottomItem(
                item = item,
                isSelected = isSelected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

@Composable
fun RowScope.CyberBottomItem(
    item: BottomBarItems,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00E5FF) else Color.Gray, // AiAccent
        label = "color"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Disable that ugly default ripple
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Neon line indicator above the icon
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(3.dp)
                .background(
                    if (isSelected) Color(0xFF00E5FF) else Color.Transparent,
                    RoundedCornerShape(1.dp)
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Icon(
            painter = painterResource(item.icon),
            contentDescription = item.title,
            tint = animatedColor,
            modifier = Modifier
                .size(24.dp)
                .scale(animatedScale)
        )

        if (isSelected) {
            Text(
                text = item.title,
                color = animatedColor,
                fontSize = 9.sp, // Чуть-чуть уменьшили
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp, // Уменьшили разрядку букв
                maxLines = 1, // ЗАПРЕЩАЕМ ПЕРЕНОС!
                overflow = TextOverflow.Ellipsis // Ставим точки, если слово слишком длинное
            )
        }
    }
}

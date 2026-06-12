package com.vdggrtf.playlog.presentation.components.mylibrary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun FairyHintWithArrow() {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 60.dp)
                .fillMaxWidth(0.85f),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .padding(top = 60.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardBackground,
                border = BorderStroke(2.dp, PrimaryPurple),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = stringResource(R.string.hello_your_library_is_empty_click_on_the_button_below_to_add_games_manually_or_through_ai_magic),
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        top = 60.dp,
                        bottom = 24.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    lineHeight = 22.sp
                )
            }

            Image(
                painter = painterResource(R.drawable.little_fairy),
                contentDescription = stringResource(R.string.gaben_fairy),
                modifier = Modifier
                    .size(130.dp)
                    .offset(y = (-20).dp)
                    .zIndex(1f),
                contentScale = ContentScale.Crop
            )
        }


        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = stringResource(R.string.look_here),
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 70.dp, bottom = 100.dp)
                .size(100.dp)
                .rotate(-35f)
        )
    }
}
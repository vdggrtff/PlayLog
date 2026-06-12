package com.vdggrtf.playlog.presentation.components.card_details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun ExpandableDescription(text: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .animateContentSize()
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = Color.LightGray,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = if (isExpanded) stringResource(R.string.hide) else stringResource(R.string.read_all),
            color = PrimaryPurple,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(top = 8.dp, bottom = 8.dp)
        )
    }
}
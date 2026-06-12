package com.vdggrtf.playlog.presentation.components.card_details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun StoreLinksRow(gameName: String, cheapestPrice: String?) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFF1E1E26), RoundedCornerShape(12.dp))
            .border(1.dp, PrimaryPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT TEXT (With weight to prevent pushing the price out)
            Text(
                text = stringResource(R.string.find_discounts_bundles),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp, // Slightly smaller
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f) // 🔥 TAKES REMAINING SPACE
            )

            // RIGHT TEXT (Price)
            if (cheapestPrice != null) {
                Text(
                    text = stringResource(R.string.from, cheapestPrice),
                    color = Color.Yellow,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp, // Slightly smaller
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    // GOG REF LINK
                    val url = "https://www.gog.com/games?query=$gameName"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDF00A2)), // Цвет GOG
                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp) // Кибер-срез
            ) {
                Text("GOG.COM", color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
            Button(
                onClick = {
                    // HUMBLE REF LINK
                    val url = "https://www.humblebundle.com/store/search?search=$gameName"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC2929)),
                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
            ) {
                Text("HUMBLE", color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
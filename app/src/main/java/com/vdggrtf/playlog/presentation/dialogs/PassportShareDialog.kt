package com.vdggrtf.playlog.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.presentation.components.profile.GamerPassportUi
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.utils.ShareUtils
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController

@Composable
fun PassportShareDialog(
    nickname: String,
    totalGames: Int,
    completedGames: Int,
    favDifficulty: String,
    customChallengesCount: Int,
    onDismiss: () -> Unit,
) {
    val captureController = rememberCaptureController()
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.your_id_card),
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Capturable(
                controller = captureController,
                onCaptured = { bitmap, error ->
                    if (bitmap != null) {
                        ShareUtils.shareImage(context, bitmap.asAndroidBitmap())
                    }
                }
            ) {
                GamerPassportUi(
                    nickname = nickname,
                    totalGames = totalGames,
                    completedGames = completedGames,
                    favDifficulty = favDifficulty,
                    customChallengesCount = customChallengesCount
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Screenshot
            Button(
                onClick = {
                    captureController.capture()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AiAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.share),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close), color = Color.Gray)
            }
        }
    }
}
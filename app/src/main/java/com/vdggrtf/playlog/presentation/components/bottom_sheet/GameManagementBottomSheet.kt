package com.vdggrtf.playlog.presentation.components.bottom_sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.presentation.components.card_details.StatusOptionRow
import com.vdggrtf.playlog.presentation.main.game_details.GameDetailsState
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameManagementBottomSheet(
    gameState: GameDetailsState,
    onDismiss: () -> Unit,
    onUpdateStatus: (GameStatus) -> Unit,
    onProveClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E26)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.game_management),
                color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (gameState.currentGameStatus == GameStatus.COMPLETED) {
                Box(modifier = Modifier.fillMaxWidth().background(PrimaryPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(16.dp)) {
                    Text(stringResource(R.string.game_completed_100_status_recorded), color = PrimaryPurple, fontWeight = FontWeight.Bold)
                }
            } else {
                StatusOptionRow(stringResource(R.string.backlog_game_details), gameState.currentGameStatus == GameStatus.BACKLOG) {
                    onUpdateStatus(GameStatus.BACKLOG)
                    onDismiss()
                }
                StatusOptionRow(stringResource(R.string.playing_game_details), gameState.currentGameStatus == GameStatus.PLAYING) {
                    onUpdateStatus(GameStatus.PLAYING)
                    onDismiss()
                }

                if (gameState.isSavedLibrary) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onDismiss(); onProveClick() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AiAccent.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, AiAccent), shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.confirm_100), color = AiAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (gameState.isSavedLibrary) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.DarkGray) // 💥 Заменил Divider на HorizontalDivider (Material 3)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onUpdateStatus(GameStatus.NONE); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30).copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color(0xFFFF3B30)), shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.remove_from_library), color = Color(0xFFFF3B30), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
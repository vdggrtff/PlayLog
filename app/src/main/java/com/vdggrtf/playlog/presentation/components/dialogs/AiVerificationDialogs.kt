package com.vdggrtf.playlog.presentation.components.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.PrimaryPurple

@Composable
fun AiVerificationDialogs(
    isThinking: Boolean,
    error: String?,
    onClearError: () -> Unit,

    // Слот для успешного состояния (потому что на разных экранах Успех выглядит по-разному!)
    isSuccess: Boolean = false,
    successDialogContent: @Composable () -> Unit = {}
) {
    // 1. DIALOG: AI ERROR
    if (error != null) {
        AlertDialog(
            onDismissRequest = onClearError,
            title = { Text(stringResource(R.string.error_scanning)) },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = onClearError) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    // 2. DIALOG: AI THINKING
    if (isThinking) {
        AlertDialog(
            onDismissRequest = { /* Ai think don't close */ },
            containerColor = CardBackground,
            title = {
                Text(
                    stringResource(R.string.neural_scan_active),
                    color = AiAccent,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = PrimaryPurple)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stringResource(R.string.analyzing_pixels), color = Color.White)
                }
            },
            confirmButton = {}
        )
    }

    // 3. DIALOG: AI SUCCESS (Вставляет тот UI, который мы передадим снаружи)
    if (isSuccess) {
        successDialogContent()
    }
}
package com.vdggrtf.playlog.presentation.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCreate: (title: String, description: String) -> Unit
) {
    if (!showDialog) return

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E26), // CardBackground
        title = { Text("Create Playlist", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Playlist Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF), unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)", color = Color.Gray) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00E5FF), unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, description)
                        title = ""
                        description = ""
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                shape = RoundedCornerShape(8.dp)
            ) { Text("CREATE", color = Color.Black, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = Color.Gray) }
        }
    )
}
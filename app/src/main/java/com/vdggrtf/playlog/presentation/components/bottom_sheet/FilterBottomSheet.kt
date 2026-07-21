package com.vdggrtf.playlog.presentation.components.bottom_sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFiltersBottomSheet(
    currentFilters: AdvancedFilters,
    showDifficultyFilter: Boolean = true,
    onApply: (AdvancedFilters) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    // Local state to hold slider values before applying
    var ratingRange by remember { mutableStateOf(currentFilters.ratingRange) }
    var yearRange by remember { mutableStateOf(currentFilters.yearRange) }
    var selectedDiff by remember { mutableStateOf(currentFilters.difficulty) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E26) // CardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "FILTERS",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. RATING SLIDER
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("RAWG Rating", color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(
                    text = "${String.format("%.1f", ratingRange.start)} - ${String.format("%.1f", ratingRange.endInclusive)}",
                    color = Color(0xFF00E5FF), // AiAccent
                    fontWeight = FontWeight.Bold
                )
            }
            RangeSlider(
                value = ratingRange,
                onValueChange = { ratingRange = it },
                valueRange = 0f..5f,
                steps = 49, // Allows 0.1 steps
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00E5FF),
                    activeTrackColor = Color(0xFF00E5FF),
                    inactiveTrackColor = Color.DarkGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. YEAR SLIDER
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Release Year", color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(
                    text = "${yearRange.start.toInt()} - ${yearRange.endInclusive.toInt()}",
                    color = Color(0xFF7C4DFF), // PrimaryPurple
                    fontWeight = FontWeight.Bold
                )
            }
            RangeSlider(
                value = yearRange,
                onValueChange = { yearRange = it },
                valueRange = 1990f..2026f,
                steps = 35, // 1 year steps
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF7C4DFF),
                    activeTrackColor = Color(0xFF7C4DFF),
                    inactiveTrackColor = Color.DarkGray
                )
            )

            if (showDifficultyFilter) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Peak Difficulty", color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    // Само поле, на которое мы нажимаем
                    OutlinedTextField(
                        value = if (selectedDiff == AchievementDifficulty.NONE) "Any Difficulty" else "${selectedDiff.title.uppercase()}",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            if (selectedDiff != AchievementDifficulty.NONE) {
                                Image(painter = painterResource(id = selectedDiff.emoji), contentDescription = null, modifier = Modifier.size(24.dp))
                            }
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F0F14),
                            unfocusedContainerColor = Color(0xFF0F0F14)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Выпадающее меню с вариантами
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E26))
                    ) {
                        val allDiffs = AchievementDifficulty.entries
                        allDiffs.forEach { diff ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (diff == AchievementDifficulty.NONE) "Any Difficulty" else diff.title.uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                leadingIcon = {
                                    if (diff != AchievementDifficulty.NONE) {
                                        Image(painter = painterResource(id = diff.emoji), contentDescription = null, modifier = Modifier.size(24.dp))
                                    }
                                },
                                onClick = {
                                    selectedDiff = diff
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // BUTTONS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = {
                        onReset()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("RESET")
                }

                Button(
                    onClick = {
                        onApply(AdvancedFilters(ratingRange, yearRange, selectedDiff))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("APPLY", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
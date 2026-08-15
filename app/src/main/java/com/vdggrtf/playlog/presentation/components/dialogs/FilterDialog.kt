package com.vdggrtf.playlog.presentation.components.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import com.vdggrtf.playlog.ui.theme.CardBackground
import com.vdggrtf.playlog.ui.theme.accentColor
import com.vdggrtf.playlog.ui.theme.bgColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFiltersScreen(
    currentFilters: AdvancedFilters,
    showDifficultyFilter: Boolean = true,
    showBountiesToggle: Boolean = true,
    onApply: (AdvancedFilters) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    // Local state to hold slider values before applying
    var ratingRange by remember { mutableStateOf(currentFilters.ratingRange) }
    var yearRange by remember { mutableStateOf(currentFilters.yearRange) }
    var selectedDiff by remember { mutableStateOf(currentFilters.difficulty) }
    var hasBounties by remember { mutableStateOf(currentFilters.hasBounties) }
    var selectedGenres by remember { mutableStateOf(currentFilters.selectedGenres) }
    var selectedPlatforms by remember { mutableStateOf(currentFilters.selectedPlatforms) }

    val allGenres = listOf("Action", "RPG", "Shooter", "Adventure", "Indie", "Strategy", "Puzzle")
    val allPlatforms = listOf("PC", "PlayStation", "Xbox", "Nintendo", "Mobile", "SEGA", "Atari")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Растягивает на 100% ширины
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Filters", color = Color.White, fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
                )
            },
            containerColor = bgColor,
            bottomBar = {
                Surface(color = CardBackground, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onReset(); onDismiss() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("RESET", color = Color.White) }

                        Button(
                            onClick = {
                                onApply(
                                    AdvancedFilters(
                                        ratingRange = ratingRange,
                                        yearRange = yearRange,
                                        difficulty = selectedDiff,
                                        hasBounties = hasBounties,
                                        selectedGenres = selectedGenres,
                                        selectedPlatforms = selectedPlatforms
                                    )
                                )
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("APPLY", color = Color.Black, fontWeight = FontWeight.Black) }
                    }
                }
            }
        ) { paddingValues ->
            // 💥 КОНТЕНТ СКРОЛЛИТСЯ, А КНОПКИ СТОЯТ НА МЕСТЕ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()) // СКРОЛЛ ДОБАВЛЕН!
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // 1. RATING
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("RAWG Rating", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(
                        "${String.format("%.1f", ratingRange.start)} - ${
                            String.format(
                                "%.1f",
                                ratingRange.endInclusive
                            )
                        }", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold
                    )
                }
                RangeSlider(
                    value = ratingRange, onValueChange = { ratingRange = it }, valueRange = 0f..5f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. YEAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Release Year", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(
                        "${yearRange.start.toInt()} - ${yearRange.endInclusive.toInt()}",
                        color = Color(0xFF7C4DFF),
                        fontWeight = FontWeight.Bold
                    )
                }
                RangeSlider(
                    value = yearRange,
                    onValueChange = { yearRange = it },
                    valueRange = 1990f..2026f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF7C4DFF),
                        activeTrackColor = Color(0xFF7C4DFF)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. BOUNTIES SWITCH
                if (showBountiesToggle) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Has Active Bounties",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Show games with custom challenges",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = hasBounties,
                            onCheckedChange = { hasBounties = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFFFF9100))
                        )
                    }
                }

                // 4. DIFFICULTY (Если включено)
                if (showDifficultyFilter) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Peak Difficulty", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (showDifficultyFilter) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Peak Difficulty", color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        var expanded by remember { mutableStateOf(false) }

                        // Filtering out CUSTOM_CHALLENGE from the list (it has its own switch!)
                        val availableDifficulties = remember {
                            AchievementDifficulty.entries.filter { it != AchievementDifficulty.CUSTOM_CHALLENGE }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            // Selected field representation
                            OutlinedTextField(
                                value = if (selectedDiff == AchievementDifficulty.NONE) {
                                    "Any Difficulty"
                                } else {
                                    selectedDiff.title.uppercase()
                                },
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = {
                                    if (selectedDiff != AchievementDifficulty.NONE) {
                                        Image(
                                            painter = painterResource(id = selectedDiff.emoji),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF00E5FF), // AiAccent
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF0F0F14), // Background
                                    unfocusedContainerColor = Color(0xFF0F0F14)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Dropdown menu options
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color(0xFF1E1E26)) // CardBackground
                            ) {
                                availableDifficulties.forEach { diff ->
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
                                                Image(
                                                    painter = painterResource(id = diff.emoji),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
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
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. GENRES
                Text("Genres", color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allGenres.forEach { genre ->
                        val isSelected = selectedGenres.contains(genre)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedGenres =
                                    if (isSelected) selectedGenres - genre else selectedGenres + genre
                            },
                            label = { Text(genre, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accentColor,
                                containerColor = CardBackground,
                                labelColor = Color.Gray,
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. PLATFORMS
                Text("Platforms", color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allPlatforms.forEach { platform ->
                        val isSelected = selectedPlatforms.contains(platform)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPlatforms =
                                    if (isSelected) selectedPlatforms - platform else selectedPlatforms + platform
                            },
                            label = { Text(platform, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(
                                    0xFF00E5FF
                                ),
                                containerColor = CardBackground,
                                labelColor = Color.Gray,
                                selectedLabelColor = Color.Black
                            ),
                            border = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
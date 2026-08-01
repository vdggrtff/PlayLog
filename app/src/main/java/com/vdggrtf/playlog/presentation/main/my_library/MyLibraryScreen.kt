package com.vdggrtf.playlog.presentation.main.my_library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.presentation.components.dialogs.AdvancedFiltersScreen
import com.vdggrtf.playlog.presentation.components.dialogs.ProofUploadDialog
import com.vdggrtf.playlog.presentation.components.list.GamesListTemplate
import com.vdggrtf.playlog.presentation.components.mylibrary.FairyHintWithArrow
import com.vdggrtf.playlog.presentation.components.mylibrary.LibraryHeader
import com.vdggrtf.playlog.presentation.main.my_library.scaner.ScannerViewModel
import com.vdggrtf.playlog.ui.theme.AiAccent
import com.vdggrtf.playlog.ui.theme.PrimaryPurple
import com.vdggrtf.playlog.ui.theme.bgColor

@Composable
fun LibraryRoute(
    onGameClick: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    libraryViewModel: MyLibraryViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
) {
    val state by libraryViewModel.state.collectAsState()
    val advancedFilters by libraryViewModel.advancedFilters.collectAsState()
    val selectedStatus by libraryViewModel.selectedStatus.collectAsState()
    val scannerStatus by scannerViewModel.statusText.collectAsState()
    var showProofDialog by remember { mutableStateOf(false) }

    // 💥 GALLERY LAUNCHER LIVES IN THE ROUTE
    ProofUploadDialog(
        showDialog = showProofDialog,
        onDismiss = { showProofDialog = false },
        onImageReady = { byteArray ->
            scannerViewModel.scanAndImportLibrary(byteArray)
        }
    )

    // CALLING THE DUMB SCREEN
    LibraryScreen(
        state = state,
        selectedStatus = selectedStatus,
        gridColumns = state.gridColumns,
        advancedFilters = advancedFilters,
        onApplyFilters = { newFilters -> libraryViewModel.applyAdvancedFilters(newFilters)},
        onResetFilters = { libraryViewModel.resetAdvancedFilters() },
        onToggleGrid = { libraryViewModel.toggleGridColumns() },
        scannerStatus = scannerStatus,
        onGameClick = onGameClick,
        onNavigateToSearch = onNavigateToSearch,
        onLaunchScanner = { showProofDialog = true },
        onClearScanner = { scannerViewModel.clearStatus() },
        onFilterStatusChanged = { libraryViewModel.setFilterStatus(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    state: LibraryState,
    selectedStatus: GameStatus,
    advancedFilters: AdvancedFilters,
    onApplyFilters: (AdvancedFilters) -> Unit,
    onResetFilters: () -> Unit,
    gridColumns: Int,
    onToggleGrid: () -> Unit,
    scannerStatus: String?,
    onGameClick: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onLaunchScanner: () -> Unit,
    onClearScanner: () -> Unit,
    onFilterStatusChanged: (GameStatus) -> Unit,
) {
    var showAddMenu by remember { mutableStateOf(false) }

    var showFilterSheet by remember { mutableStateOf(false) }



    // scanner dialog (find and add game or not)
    if (scannerStatus != null) {
        Dialog(onDismissRequest = onClearScanner) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = com.vdggrtf.playlog.ui.theme.CardBackground,
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (scannerStatus!!.startsWith("✨") || scannerStatus!!.contains("🔎")) {
                        CircularProgressIndicator(color = PrimaryPurple)
                        Spacer(Modifier.height(16.dp))
                    }
                    Text(
                        text = scannerStatus!!,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    if (scannerStatus!!.startsWith("✅") || scannerStatus!!.startsWith("❌")) {
                        Spacer(Modifier.height(16.dp))
                        androidx.compose.material3.TextButton(onClick = onClearScanner) {
                            Text(
                                stringResource(R.string.close_library_screen),
                                color = PrimaryPurple
                            )
                        }
                    }
                }
            }
        }
    }


    Scaffold(
        containerColor = bgColor,
        floatingActionButton = {
            // FAB
            Column(horizontalAlignment = Alignment.End) {
                // showing two options (AI and Manual)
                if (showAddMenu) {
                    FloatingActionButton(
                        onClick = {
                            showAddMenu = false
                            onLaunchScanner()
                        },
                        containerColor = AiAccent,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text("✨ AI", fontWeight = FontWeight.Bold)
                    }
                    FloatingActionButton(
                        onClick = { showAddMenu = false; onNavigateToSearch() },
                        containerColor = PrimaryPurple,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.manual),
                            tint = Color.White
                        )
                    }
                }

                // Main FAB
                FloatingActionButton(
                    onClick = { showAddMenu = !showAddMenu },
                    containerColor = PrimaryPurple,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_game),
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        GamesListTemplate(
            title = stringResource(R.string.library),
            isLoading = state.isLoading,
            games = state.displayedGames,
            gridColumns = gridColumns,
            onAdvancedFilterClick = { showFilterSheet = true },
            onToggleGridClick = onToggleGrid,
            headerContent = {
                Column {
                    // header with the progress bar
                    LibraryHeader(
                        allGames = state.games,
                        selectedStatus = selectedStatus,
                        onStatusSelected = onFilterStatusChanged
                    )
                }
            },
            emptyStateContent = {
                if (state.games.isEmpty()) {
                    FairyHintWithArrow()
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "It's empty here for now. 🕵️‍♂️",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            onGameClick = onGameClick
        )
        if (showFilterSheet) {
            AdvancedFiltersScreen(
                currentFilters = advancedFilters,
                showDifficultyFilter = true,
                onApply = { newFilters ->
                    onApplyFilters(newFilters)
                    showFilterSheet = false
                },
                onReset = {
                    onResetFilters()
                    showFilterSheet = false
                },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}


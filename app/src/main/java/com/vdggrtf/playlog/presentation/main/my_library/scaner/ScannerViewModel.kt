package com.vdggrtf.playlog.presentation.main.my_library.scaner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val gamesRepository: GameRepository,
    private val libraryRepository: LibraryRepository,
    private val aiRepository: AiRepository,
) : ViewModel() {

    private val _statusText = MutableStateFlow<String?>(null)
    val statusText = _statusText.asStateFlow()

    fun scanAndImportLibrary(imageBytes: ByteArray) {
        viewModelScope.launch {
            try {


                _statusText.value = "✨ ai read screenshot..."

                val gameNames = aiRepository.scanLibraryForGames(imageBytes)

                if (gameNames.isEmpty()) {
                    _statusText.value = "❌ games not fond in screenshot"
                    return@launch
                }

                _statusText.value = "🔎 search ${gameNames.size} games in RAWG database..."

                // Find this games in RAWG parallel
                val gamesToSave = coroutineScope {
                    gameNames.map { name ->
                        async {
                            // Searching for the game and taking the first relevant result.
                            gamesRepository.searchGames(name, 1).getOrNull()?.firstOrNull()
                        }
                    }.awaitAll().filterNotNull()
                }

                if (gamesToSave.isEmpty()) {
                    _statusText.value = "❌ Games are recognized but not found in the RAWG database."
                    return@launch
                }

                _statusText.value = "💾 Save in library..."

                // Saving all found games to Room and Supabase.
                gamesToSave.forEach { game ->
                    val gameToSave = game.copy(
                        status = GameStatus.BACKLOG,
                        aiDifficulty = AchievementDifficulty.NONE,
                        userDifficulty = AchievementDifficulty.NONE,
                        verifiedDifficulty = AchievementDifficulty.NONE
                    )
                    libraryRepository.addGameToLibrary(gameToSave)
                }

                _statusText.value = "✅ success import ${gamesToSave.size} games!"
            } catch (e: Exception) {
                _statusText.value = "❌ scan error: ${e.message}"
            }
        }
    }

    fun clearStatus() {
        _statusText.value = null
    }
}
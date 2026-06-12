package com.vdggrtf.playlog.presentation.main.my_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryState (
    val isLoading: Boolean = false,
    val games: List<GameModel> = emptyList(),
    val displayedGames: List<GameModel> = emptyList()
)

@HiltViewModel
class MyLibraryViewModel @Inject constructor(private val repository: LibraryRepository) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    private val _selectedStatus = MutableStateFlow<GameStatus>(GameStatus.COMPLETED)
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _selectedDifficultyFilter = MutableStateFlow<AchievementDifficulty?>(null)
    val selectedDifficultyFilter = _selectedDifficultyFilter.asStateFlow()

    init {
        showMyLibrary()

        // filter games by status and difficulty
        viewModelScope.launch {
            combine(_state, _selectedStatus, _selectedDifficultyFilter) { currentState, currentStatus, diffFilter ->
                var filtered = currentState.games.filter { it.status == currentStatus }

                if (diffFilter != null){
                    filtered = filtered.filter {
                        it.verifiedDifficulty == diffFilter || it.aiDifficulty == diffFilter
                    }
                }

                currentState.copy(displayedGames = filtered)
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    private fun showMyLibrary() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.getMyLibrary().collect { gameModels ->

                _state.update { it.copy(isLoading = false, games = gameModels) }

            }
        }
    }

    fun toggleDifficultyFilter(difficulty: AchievementDifficulty){
        if (_selectedDifficultyFilter.value == difficulty){
            _selectedDifficultyFilter.value = null
        } else {
            _selectedDifficultyFilter.value = difficulty
        }
    }

    fun setFilterStatus(status: GameStatus) {
        _selectedStatus.value = status
    }
}
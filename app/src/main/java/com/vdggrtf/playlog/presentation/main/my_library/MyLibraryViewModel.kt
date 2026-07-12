package com.vdggrtf.playlog.presentation.main.my_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.usecase.library.GetCompletedBountiesCountUseCase
import com.vdggrtf.playlog.domain.usecase.library.ObserveMyLibraryUseCase
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
    val displayedGames: List<GameModel> = emptyList(),
    val completedBountiesCount: Int = 0
)

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val observeMyLibraryUseCase: ObserveMyLibraryUseCase,
    private val getCompletedBountiesCountUseCase: GetCompletedBountiesCountUseCase
) : ViewModel() {

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

                // 1. Fetch completed challenges from Supabase in a parallel coroutine.
            // This prevents the network request from blocking our offline-first Room database loading!
            launch {
                val count = getCompletedBountiesCountUseCase()
                _state.update { it.copy(completedBountiesCount = count) }
            }

            // 2. Collect local library games from Room.
            // Since 'collect' is a terminal operator that runs indefinitely, it must be launched last.
            observeMyLibraryUseCase().collect { gameModels ->
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
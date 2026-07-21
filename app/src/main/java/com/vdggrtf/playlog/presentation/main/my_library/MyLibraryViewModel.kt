package com.vdggrtf.playlog.presentation.main.my_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.usecase.main.library.GetCompletedBountiesCountUseCase
import com.vdggrtf.playlog.domain.usecase.main.library.ObserveMyLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter

data class AdvancedFilters(
    val ratingRange: ClosedFloatingPointRange<Float> = 0f..5f,
    val yearRange: ClosedFloatingPointRange<Float> = 1990f..2026f,
    val difficulty: AchievementDifficulty = AchievementDifficulty.NONE,
)

data class LibraryState(
    val isLoading: Boolean = false,
    val games: List<GameModel> = emptyList(),
    val displayedGames: List<GameModel> = emptyList(),
    val completedBountiesCount: Int = 0,
    val gridColumns: Int = 2,
)

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val observeMyLibraryUseCase: ObserveMyLibraryUseCase,
    private val getCompletedBountiesCountUseCase: GetCompletedBountiesCountUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    private val _selectedStatus = MutableStateFlow<GameStatus>(GameStatus.COMPLETED)
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _selectedDifficultyFilter = MutableStateFlow<AchievementDifficulty?>(null)
    val selectedDifficultyFilter = _selectedDifficultyFilter.asStateFlow()

    private val _advancedFilters = MutableStateFlow(AdvancedFilters())
    val advancedFilters = _advancedFilters.asStateFlow()

    init {
        showMyLibrary()

        // filter games by status and difficulty
        viewModelScope.launch {
            combine(
                _state.map { it.games }.distinctUntilChanged(),
                _selectedStatus,
                _advancedFilters
            ) { gamesList, currentStatus, advancedFilters ->

                var filtered = gamesList

                filtered = filtered.filter { it.status == currentStatus }

                if (advancedFilters.difficulty != AchievementDifficulty.NONE) {
                    filtered = filtered.filter { game ->
                        val actualDifficulty = if (game.verifiedDifficulty != AchievementDifficulty.NONE) {
                            game.verifiedDifficulty
                        } else {
                            game.aiDifficulty
                        }

                        actualDifficulty == advancedFilters.difficulty
                    }
                }

                filtered = filtered.filter { game ->
                    val rating = game.rating?.toFloat() ?: 0f
                    val year = game.releasedDate?.take(4)?.toFloatOrNull() ?: 1990f

                    rating in advancedFilters.ratingRange && year in advancedFilters.yearRange
                }

                filtered
            }.collect { resultList ->
                _state.update { it.copy(displayedGames = resultList) }
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

    fun toggleDifficultyFilter(difficulty: AchievementDifficulty) {
        if (_selectedDifficultyFilter.value == difficulty) {
            _selectedDifficultyFilter.value = null
        } else {
            _selectedDifficultyFilter.value = difficulty
        }
    }

    fun toggleGridColumns() {
        _state.update { currentState ->
            val nextColumns = when (currentState.gridColumns) {
                1 -> 2
                2 -> 4
                4 -> 1
                else -> 2
            }
            currentState.copy(gridColumns = nextColumns)
        }
    }

    // Function to apply new filters from the UI
    fun applyAdvancedFilters(newFilters: AdvancedFilters) {
        _advancedFilters.value = newFilters
    }

    // Function to reset filters
    fun resetAdvancedFilters() {
        _advancedFilters.value = AdvancedFilters()
    }

    fun setFilterStatus(status: GameStatus) {
        _selectedStatus.value = status
    }
}
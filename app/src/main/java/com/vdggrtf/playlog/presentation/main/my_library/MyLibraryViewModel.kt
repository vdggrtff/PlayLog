package com.vdggrtf.playlog.presentation.main.my_library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.data.network.dto.ChallengeDto
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.usecase.main.challenge.GetTrackedBountyGameIdsUseCase
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
    val hasBounties: Boolean = false,
    val selectedGenres: List<String> = emptyList(),
    val selectedPlatforms: List<String> = emptyList(),
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
    private val getTrackedBountyGameIdsUseCase: GetTrackedBountyGameIdsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    private val _selectedStatus = MutableStateFlow<GameStatus>(GameStatus.COMPLETED)
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _advancedFilters = MutableStateFlow(AdvancedFilters())
    val advancedFilters = _advancedFilters.asStateFlow()

    private val _gamesWithCompletedChallenges = MutableStateFlow<Set<Int>>(emptySet())
    val gamesWithCompletedChallenges = _gamesWithCompletedChallenges.asStateFlow()

    init {
        showMyLibrary()

        // filter games by status and difficulty
        viewModelScope.launch {
            combine(
                _state.map { it.games }.distinctUntilChanged(),
                _selectedStatus,
                _advancedFilters,
                _gamesWithCompletedChallenges
            ) { gamesList, currentStatus, advancedFilters, gamesWithBounties ->

                var filtered = gamesList
                Log.d("FILTER_DEBUG", "1. Исходно игр: ${filtered.size}")

                // status filter
                filtered = filtered.filter { it.status == currentStatus }
                Log.d("FILTER_DEBUG", "2. После статуса ($currentStatus): ${filtered.size}")

                // difficulty filter
                if (advancedFilters.difficulty != AchievementDifficulty.NONE) {
                    filtered = filtered.filter { game ->
                        val actualDifficulty =
                            if (game.verifiedDifficulty != AchievementDifficulty.NONE) {
                                game.verifiedDifficulty
                            } else {
                                game.aiDifficulty
                            }

                        actualDifficulty == advancedFilters.difficulty
                    }
                }
                Log.d("FILTER_DEBUG", "3. После сложности: ${filtered.size}")

                // year and rating filter
                filtered = filtered.filter { game ->
                    val rating = game.rating?.toFloat() ?: 0f
                    val year = game.releasedDate?.take(4)?.toFloatOrNull() ?: 1990f

                    rating in advancedFilters.ratingRange && year in advancedFilters.yearRange
                }
                Log.d("FILTER_DEBUG", "6. ФИНАЛ (После рейтинга и года): ${filtered.size}")

                // genre filter
                if (advancedFilters.selectedGenres.isNotEmpty()) {
                    filtered = filtered.filter { game ->
                        game.genres.any { gameGenre ->
                            advancedFilters.selectedGenres.any { selectedGenre ->
                                gameGenre.contains(selectedGenre, ignoreCase = true)
                            }
                        }
                    }
                }
                Log.d("FILTER_DEBUG", "4. После жанров: ${filtered.size}")

                // platforms filter
                if (advancedFilters.selectedPlatforms.isNotEmpty()) {
                    filtered = filtered.filter { game ->
                        game.platforms.any { gamePlatform ->
                            advancedFilters.selectedPlatforms.any { selectedPlatform ->
                                if (selectedPlatform == "Mobile") {
                                    gamePlatform.contains("Android", ignoreCase = true) ||
                                            gamePlatform.contains("iOS", ignoreCase = true)
                                } else {
                                    gamePlatform.contains(selectedPlatform, ignoreCase = true)
                                }
                            }
                        }
                    }
                }
                Log.d("FILTER_DEBUG", "platforms filter: ${filtered.size}")

                // have game challenge or not
                if (advancedFilters.hasBounties){
                    filtered = filtered.filter { gamesWithBounties.contains(it.id) }
                }
                Log.d("FILTER_DEBUG", "5. После свитча Bounties: ${filtered.size}")

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

            // 💥 2. НОВОЕ: ЗАПОЛНЯЕМ НАШУ КОРОБКУ ДЛЯ ФИЛЬТРА!
            launch {
                val gameIds = getTrackedBountyGameIdsUseCase()
                Log.d("BOUNTY_DEBUG", "💥 Скачали ID игр с контрактами: $gameIds")
                _gamesWithCompletedChallenges.value = gameIds
            }

            // 2. Collect local library games from Room.
            // Since 'collect' is a terminal operator that runs indefinitely, it must be launched last.
            observeMyLibraryUseCase().collect { gameModels ->
                _state.update { it.copy(isLoading = false, games = gameModels) }
            }
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
package com.vdggrtf.playlog.presentation.main.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.PlaylistModel
import com.vdggrtf.playlog.domain.usecase.main.playlist.ObserveMyPlaylistsUseCase
import com.vdggrtf.playlog.domain.usecase.main.recommendation.GetIndieGamesUseCase
import com.vdggrtf.playlog.domain.usecase.main.recommendation.GetPopularGamesUseCase
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecommendationState (
    val isLoading: Boolean = false,
    val popularGames: List<GameModel> = emptyList(),
    val error: String? = null,
    val indieGames: List<GameModel> = emptyList(),
    val playlists: List<PlaylistModel> = emptyList(),
    val gridColumns: Int = 2,
)

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val getPopularGamesUseCase: GetPopularGamesUseCase,
    private val getIndieGamesUseCase: GetIndieGamesUseCase,
    private val observeMyPlaylistsUseCase: ObserveMyPlaylistsUseCase
): ViewModel() {

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    private val _advancedFilters = MutableStateFlow(AdvancedFilters())
    val advancedFilters = _advancedFilters.asStateFlow()

    private var currentPage = 1

    init {
        loadPopularGames()

        viewModelScope.launch {
            getIndieGamesUseCase().fold(
                onSuccess = { games -> _state.update { it.copy(indieGames = games) } },
                onFailure = { /* игнорим или пишем в лог */ }
            )
        }

        viewModelScope.launch {
            observeMyPlaylistsUseCase().collect { myLists ->
                _state.update { it.copy(playlists = myLists) }
            }
        }
    }

    fun loadPopularGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Executing the UseCase like a simple function
            val result = getPopularGamesUseCase(page = 1, filters = _advancedFilters.value)

            result.fold(
                onSuccess = {games ->
                    _state.update { it.copy(popularGames = games, isLoading = false) }
                },
                onFailure = {err ->
                    _state.update { it.copy(error = err.message, isLoading = false) }
                }
            )
        }
    }

    fun loadMoreGames() {
        if (_state.value.isLoading) return // spam request protection

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            currentPage++

            getPopularGamesUseCase(page = currentPage, filters = _advancedFilters.value).fold(
                onSuccess = { newGames ->
                    // merging old and new games!
                    val updatedList = _state.value.popularGames + newGames
                    _state.update { it.copy(popularGames = updatedList, isLoading = false) }
                },
                onFailure = { error ->
                    // if a network error occurs, we roll back the page to try again.
                    currentPage--
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            )
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

    fun applyAdvancedFilters(newFilters: AdvancedFilters) {
        _advancedFilters.value = newFilters
        currentPage = 1
        loadPopularGames()
    }

    fun resetAdvancedFilters() {
        _advancedFilters.value = AdvancedFilters()
        currentPage = 1
        loadPopularGames()
    }
}
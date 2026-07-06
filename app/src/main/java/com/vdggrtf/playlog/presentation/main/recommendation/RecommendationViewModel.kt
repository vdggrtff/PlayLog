package com.vdggrtf.playlog.presentation.main.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.domain.usecase.GetPopularGamesUseCase
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
    val error: String? = null
)

@HiltViewModel
class RecommendationViewModel @Inject constructor(private val getPopularGamesUseCase: GetPopularGamesUseCase): ViewModel() {

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    private var currentPage = 1

    init {
        loadPopularGames()
    }

    fun loadPopularGames() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Executing the UseCase like a simple function
            val result = getPopularGamesUseCase(page = 1)

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

            getPopularGamesUseCase(page = currentPage).fold(
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
}
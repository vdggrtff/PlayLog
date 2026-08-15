package com.vdggrtf.playlog.presentation.main.recommendation.see_all

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.a
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.usecase.main.recommendation.GetIndieGamesUseCase
import com.vdggrtf.playlog.domain.usecase.main.recommendation.GetPopularGamesUseCase
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SeeAllState(
    val title: String = "",
    val category: String = "",
    val isLoading: Boolean = false,
    val games: List<GameModel> = emptyList(),
    val gridColumns: Int = 2,
    val error: String? = null
)

@HiltViewModel
class SeeAllGamesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPopularGamesUseCase: GetPopularGamesUseCase,
    private val getIndieGamesUseCase: GetIndieGamesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SeeAllState())
    val state = _state.asStateFlow()

    private val _advancedFilters = MutableStateFlow(AdvancedFilters())
    val advancedFilters = _advancedFilters.asStateFlow()

    private var currentPage = 1

    init {
        // Вытаскиваем категорию из навигатора
        val category = savedStateHandle.get<String>("category") ?: "popular"
        val title = if (category == "indie") "Indie Gems" else "Popular Now"

        _state.update { it.copy(category = category, title = title) }
        loadGames()
    }

    fun loadMore() {
        if (_state.value.isLoading) return
        currentPage++
        loadGames()
    }

    private fun loadGames(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = if (_state.value.category == "indie") {
                getIndieGamesUseCase(page = currentPage)
            } else {
                getPopularGamesUseCase(page = currentPage, filters = _advancedFilters.value)
            }

            result.fold(
                onSuccess = { newGames ->
                    val updatedList = if (currentPage == 1) newGames else _state.value.games + newGames
                    _state.update { it.copy(games = updatedList, isLoading = false) }
                },
                onFailure = { error ->
                    if (currentPage > 1) currentPage--
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }


    }

    fun toggleGridColumns() {
        _state.update { it.copy(gridColumns = when (it.gridColumns) { 1 -> 2; 2 -> 4; 4 -> 1; else -> 2 }) }
    }

    fun applyAdvancedFilters(newFilters: AdvancedFilters) {
        _advancedFilters.value = newFilters
        currentPage = 1
        loadGames()
    }

    fun resetAdvancedFilters() {
        _advancedFilters.value = AdvancedFilters()
        currentPage = 1
        loadGames()
    }


}


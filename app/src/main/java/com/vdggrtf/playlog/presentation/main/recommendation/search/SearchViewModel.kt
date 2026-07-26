package com.vdggrtf.playlog.presentation.main.recommendation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.usecase.main.search.SearchGamesUseCase
import com.vdggrtf.playlog.presentation.main.my_library.AdvancedFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val isLoading: Boolean = false,
    val query: String = "",
    val error: String? = null,
    val searchResult: List<GameModel> = emptyList(),
    val gridColumns: Int = 2,
)


@HiltViewModel
class SearchViewModel @Inject constructor(private val searchGamesUseCase: SearchGamesUseCase) :
    ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _advancedFilters = MutableStateFlow(AdvancedFilters())
    val advancedFilters = _advancedFilters.asStateFlow()

    private var currentPage = 1

    init {

        //search with debounce (0.5)
        viewModelScope.launch {
            _state.map { it.query }
                .distinctUntilChanged()
                .debounce(500L)
                .filter { it.isNotBlank() }
                .collect { query ->
                    currentPage = 1
                    _state.update { it.copy(searchResult = emptyList()) }
                    performSearch(query, currentPage)

                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        if (newQuery.isBlank()) {
            _state.update { it.copy(searchResult = emptyList(), isLoading = false) }
        }
    }

    fun loadMore() {
        val currentQuery = _state.value.query
        if (currentQuery.isBlank() || _state.value.isLoading) return

        currentPage++
        performSearch(currentQuery, currentPage)
    }


    private fun performSearch(query: String, page: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            searchGamesUseCase(query, page, _advancedFilters.value).fold(
                onSuccess = { games ->
                    val updateList = if (page == 1) games else _state.value.searchResult + games

                    _state.update { it.copy(searchResult = updateList, isLoading = false) }
                },
                onFailure = { error ->
                    if (page > 1) currentPage--
                    _state.update { it.copy(error = error.message, isLoading = false) }

                })
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

        val currentQuery = _state.value.query
        if (currentQuery.isNotBlank()) {
            _state.update { it.copy(searchResult = emptyList()) }
            performSearch(currentQuery, currentPage)
        }
    }

    fun resetAdvancedFilters() {
        _advancedFilters.value = AdvancedFilters()
        currentPage = 1

        val currentQuery = _state.value.query

        if (currentQuery.isNotBlank()) {
            _state.update { it.copy(searchResult = emptyList()) }
            performSearch(currentQuery, currentPage)
        }
    }
}
package com.vdggrtf.playlog.presentation.main.recommendation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
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
)


@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: GameRepository) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private var currentPage = 1

    init {

        //search with debounce (0.5)
        viewModelScope.launch {
            _state.map { it.query }
                .distinctUntilChanged()
                .debounce(500L)
                .filter { it.isNotBlank() }
                .collect { query ->
                    if (query.isNotBlank()) {
                        currentPage = 1
                        _state.update { it.copy(searchResult = emptyList()) }
                        performSearch(query, currentPage)
                    } else {
                        _state.update { it.copy(searchResult = emptyList(), isLoading = false) }
                    }
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
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

            repository.searchGames(query, page).fold(
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
}
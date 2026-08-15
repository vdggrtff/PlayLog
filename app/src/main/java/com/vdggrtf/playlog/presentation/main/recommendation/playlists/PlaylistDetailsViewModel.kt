package com.vdggrtf.playlog.presentation.main.recommendation.playlists

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.usecase.main.playlist.ObservePlaylistGamesUseCase
import com.vdggrtf.playlog.domain.usecase.main.playlist.RemoveGameFromPlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistDetailsState(
    val isLoading: Boolean = false,
    val playlistTitle: String = "Playlist",
    val games: List<GameModel> = emptyList(),
)

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observePlaylistGamesUseCase: ObservePlaylistGamesUseCase,
    private val removeGameFromPlaylistUseCase: RemoveGameFromPlaylistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistDetailsState())
    val state = _state.asStateFlow()

    private val currentPlaylistId = savedStateHandle.get<String>("playlistId") ?: ""

    init {
        val playlistId = savedStateHandle.get<String>("playlistId") ?: ""
        val title = savedStateHandle.get<String>("playlistTitle") ?: "Playlist"

        _state.update { it.copy(playlistTitle = title) }

        if (playlistId.isNotBlank()) {
            loadGames(playlistId)
        }
    }

    private fun loadGames(playlistId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observePlaylistGamesUseCase(playlistId).collect { gameList ->
                _state.update { it.copy(games = gameList, isLoading = false) }
            }
        }
    }

    fun removeGame(gameId: Int) {
        if (currentPlaylistId.isBlank()) return
        viewModelScope.launch {
            removeGameFromPlaylistUseCase(currentPlaylistId, gameId).fold(
                onSuccess = { Log.d("Playlist", "Game Removed!") }, // Flow сам обновит UI!
                onFailure = { Log.e("Playlist", "Error removing game: ${it.message}") }
            )
        }
    }
}
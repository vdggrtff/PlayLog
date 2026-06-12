package com.vdggrtf.playlog.presentation.main.recommendation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiRecommendedGame(
    val aiReason: String,
    val gameDetails: GameModel?,
)

data class AiGameState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val recommendations: List<AiRecommendedGame> = emptyList(),
)

@HiltViewModel
class AiRecommendationGameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val aiRepository: AiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AiGameState())
    val state = _state.asStateFlow()

    fun askAiForRecommendations(userPrompt: String) {
        if (userPrompt.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, recommendations = emptyList()) }

            try {
                val aiList = aiRepository.getGameRecommendation(userPrompt)

                if (aiList.isEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "No recommendations found",
                            recommendations = emptyList()
                        )
                    }

                    return@launch
                }

                coroutineScope {
                    val gamesWithDetails = aiList.map { aiGameRecommendation ->
                        async {


                            val searchResult = repository.searchGames(aiGameRecommendation.gameName)
                            val realGame = searchResult.getOrNull()?.firstOrNull()

                            AiRecommendedGame(
                                aiReason = aiGameRecommendation.reason,
                                gameDetails = realGame
                            )
                        }
                    }.awaitAll()

                    _state.update { it.copy(isLoading = false, recommendations = gamesWithDetails) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "System error ${e.message}") }
            }
        }
    }
}
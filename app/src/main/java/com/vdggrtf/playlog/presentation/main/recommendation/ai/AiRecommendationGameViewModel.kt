package com.vdggrtf.playlog.presentation.main.recommendation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.usecase.main.ai.GetAiRecommendationsWithDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getAiRecommendationsWithDetailsUseCase: GetAiRecommendationsWithDetailsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AiGameState())
    val state = _state.asStateFlow()

    fun askAiForRecommendations(userPrompt: String) {
        if (userPrompt.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, recommendations = emptyList()) }

            val result = getAiRecommendationsWithDetailsUseCase(userPrompt)

            result.fold(
                onSuccess = { games ->
                    _state.update { it.copy(isLoading = false, recommendations = games) }
                },
                onFailure = {error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }
}
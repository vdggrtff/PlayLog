package com.vdggrtf.playlog.presentation.main.my_library.scaner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.usecase.ai.VerifyGameCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerificationState(
    val isThinking: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val verifyGameCompletionUseCase: VerifyGameCompletionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(VerificationState())
    val state = _state.asStateFlow()

    fun verifyAndCompleteGame(
        imageBytes: ByteArray,
        game: GameModel,
        aiDifficulty: AchievementDifficulty, // Passing the difficulty that the AI calculated earlier.
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isThinking = true, error = null, isSuccess = false) }

            verifyGameCompletionUseCase(imageBytes, game, aiDifficulty).fold(
                onSuccess = { isApproved ->
                    if (isApproved) {
                        _state.update { it.copy(isThinking = false, isSuccess = true) }
                    } else {
                        _state.update {
                            it.copy(
                                isThinking = false,
                                error = "Скриншот не подтвержден! Загрузите четкое фото 100% комплита."
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(isThinking = false, error = error.message) }
                }
            )
        }
    }

    fun resetSuccessState() {
        _state.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
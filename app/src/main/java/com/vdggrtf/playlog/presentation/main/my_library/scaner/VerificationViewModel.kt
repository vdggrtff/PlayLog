package com.vdggrtf.playlog.presentation.main.my_library.scaner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerificationState(
    val isThinking: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VerificationState())
    val state = _state.asStateFlow()

    fun verifyAndCompleteGame(
        imageBytes: ByteArray,
        game: GameModel,
        aiDifficulty: AchievementDifficulty // Passing the difficulty that the AI calculated earlier.
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isThinking = true, error = null, isSuccess = false) }

            try {
                Log.i("VerificationVM", "[AI Scanner]: Проверка скриншота для ${game.name}")

                // Ask Ai
                val isVerified = aiRepository.verifyScreenshot(imageBytes, game.name)

                if (isVerified) {
                    Log.d("VerificationVM", "[AI Scanner]: Скриншот подтвержден! Сохраняем...")

                    val completedGame = game.copy(
                        status = GameStatus.COMPLETED,
                        aiDifficulty = aiDifficulty,
                        verifiedDifficulty = aiDifficulty
                    )
                    libraryRepository.addGameToLibrary(completedGame)

                    _state.update { it.copy(isThinking = false, isSuccess = true) }
                } else {
                    Log.w("VerificationVM", "[AI Scanner]: Скриншот отклонен.")
                    _state.update {
                        it.copy(isThinking = false, error = "Скриншот не подтвержден! Загрузите четкое фото 100% комплита.")
                    }
                }
            } catch (e: Exception) {
                Log.e("VerificationVM", "[AI Scanner Error]: ${e.message}")
                _state.update { it.copy(isThinking = false, error = "Ошибка сети или ИИ") }
            }
        }
    }

    fun verifyCustomChallenge(
        imageBytes: ByteArray,
        challenge: CustomChallengeModel,
        gameName: String // We need the game name for the prompt context
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isThinking = true, error = null, isSuccess = false) }

            try {
                Log.i("VerificationVM", "[Bounty Scanner]: Checking proof for ${challenge.title}")

                // Call the new method we added to AiRepository yesterday
                val isVerified = aiRepository.verifyCustomChallenge(
                    imageBytes = imageBytes,
                    gameName = gameName,
                    challengePrompt = challenge.aiPrompt
                )

                if (isVerified) {
                    Log.d("VerificationVM", "[Bounty Scanner]: SUCCESS! Challenge completed.")

                    // TODO: Here we will later write to Supabase 'user_completed_challenges'

                    _state.update { it.copy(isThinking = false, isSuccess = true) }
                } else {
                    Log.w("VerificationVM", "[Bounty Scanner]: REJECTED by AI.")
                    _state.update {
                        it.copy(
                            isThinking = false,
                            error = "Proof rejected! Ensure your screenshot clearly shows the required condition."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("VerificationVM", "[Bounty Scanner Error]: ${e.message}")
                _state.update { it.copy(isThinking = false, error = "AI Network Error") }
            }
        }
    }

    fun resetSuccessState() {
        _state.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
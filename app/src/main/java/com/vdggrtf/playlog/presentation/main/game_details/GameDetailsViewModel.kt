package com.vdggrtf.playlog.presentation.main.game_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.data.network.dto.AchievementDto
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.usecase.main.challenge.GetChallengesForGameUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.ChangeGameStatusUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.FetchAndSyncRemoteGameUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.FinishGameWithRatingUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.GetBestGameDealUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.GetCommunityRatingUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.GetLocalGameUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.ObserveLocalGameStatusUseCase
import com.vdggrtf.playlog.domain.usecase.main.game.RetryAiEvaluationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameDetailsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val game: GameModel? = null,
    val screenshots: List<String> = emptyList(),
    val achievements: List<AchievementDto> = emptyList(),
    val isSavedLibrary: Boolean = false,
    val currentGameStatus: GameStatus = GameStatus.NONE,
    val objectiveDifficulty: AchievementDifficulty = AchievementDifficulty.NONE,
    val isAiThinking: Boolean = true,
    val currentGameStatusDifficulty: AchievementDifficulty = AchievementDifficulty.NONE,
    val cheapestPrice: String? = null,
    val dealUrl: String? = null,
    val communityDifficulty: AchievementDifficulty = AchievementDifficulty.NONE,
    val communityVotesCount: Int = 0,
    val customChallenges: List<CustomChallengeModel> = emptyList(),
    val isChallengeVerifying: Boolean = false,
)


@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val getLocalGameUseCase: GetLocalGameUseCase,
    private val observeLocalGameStatusUseCase: ObserveLocalGameStatusUseCase,
    private val retryAiEvaluationUseCase: RetryAiEvaluationUseCase,
    private val finishGameWithRatingUseCase: FinishGameWithRatingUseCase,
    private val changeGameStatusUseCase: ChangeGameStatusUseCase,
    private val fetchAndSyncRemoteGameUseCase: FetchAndSyncRemoteGameUseCase,
    private val getChallengesForGameUseCase: GetChallengesForGameUseCase,
    savedStateHandle: SavedStateHandle,
    private val getBestGameDealUseCase: GetBestGameDealUseCase,
    private val getCommunityRatingUseCase: GetCommunityRatingUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(GameDetailsState())
    val state = _state.asStateFlow()

    init {
        val gameId = savedStateHandle.get<Int>("gameId") ?: -1
        if (gameId != -1) {
            loadGameDetails(gameId)
            checkIfGameInMyLibrary(gameId)
            loadCommunityRating(gameId)
            loadGameChallenges(gameId)
        } else {
            _state.update { it.copy(isLoading = false, error = "Invalid game ID") }
        }
    }

    private fun loadGameDetails(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isAiThinking = true) }

            //(Offline-First)
            val localGame = getLocalGameUseCase(id)
            if (localGame != null) {
                _state.update {
                    it.copy(
                        game = localGame,
                        objectiveDifficulty = localGame.aiDifficulty,
                        isSavedLibrary = true
                    )
                }
            }

            // REMOTE ORCHESTRATOR: Smart Merge, RAWG, Cache
            val result = fetchAndSyncRemoteGameUseCase(id, localGame)

            result.fold(
                onSuccess = { remoteData ->
                    _state.update {
                        it.copy(
                            game = remoteData.game,
                            screenshots = remoteData.screenshots,
                            achievements = remoteData.achievements,
                            objectiveDifficulty = remoteData.objectiveDifficulty,
                            isAiThinking = false
                        )
                    }

                    // LOAD DEALS (CheapShark)
                    loadCheapShark(remoteData.game.name)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            error = "Сбой загрузки данных: ${error.message}",
                            isAiThinking = false
                        )
                    }
                }
            )

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun checkIfGameInMyLibrary(id: Int) {
        viewModelScope.launch {
            observeLocalGameStatusUseCase(id).collect { savedGame ->
                if (savedGame != null) {
                    _state.update {
                        it.copy(
                            isSavedLibrary = true,
                            currentGameStatus = savedGame.status,
                            // REMEMBERING THE DIFFICULTY FROM THE DATABASE:
                            currentGameStatusDifficulty = savedGame.aiDifficulty
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSavedLibrary = false,
                            currentGameStatus = GameStatus.NONE,
                            // RESETTING THE DIFFICULTY:
                            currentGameStatusDifficulty = AchievementDifficulty.NONE
                        )
                    }
                }
            }
        }
    }

    fun updateCurrentStatus(newStatus: GameStatus) {
        val currentGame = _state.value.game ?: return
        val currentAiDiff = _state.value.objectiveDifficulty

        viewModelScope.launch {
            changeGameStatusUseCase(currentGame, newStatus, currentAiDiff)
            // Note: The UI will update automatically because checkIfGameInMyLibrary collects the Flow!
        }
    }

    fun completeGameWithUserRating(userDiff: AchievementDifficulty) {
        val currentGame = _state.value.game ?: return
        val aiDiff = _state.value.objectiveDifficulty

        viewModelScope.launch {
            finishGameWithRatingUseCase(userDiff, currentGame, aiDiff)
            loadCommunityRating(currentGame.id) // Reload community rating after we vote
        }
    }

    private fun loadCommunityRating(gameId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val (commDiff, votesCount) = getCommunityRatingUseCase(gameId)

            _state.update {
                it.copy(communityDifficulty = commDiff, communityVotesCount = votesCount)
            }
        }
    }

    fun retryAiEvaluation() {
        val game = _state.value.game ?: return

        viewModelScope.launch {
            // Progress Circle (Ai thinking)
            _state.update { it.copy(isAiThinking = true) }

            retryAiEvaluationUseCase(game.id, game.name).fold(
                onSuccess = { aiDiff ->
                    _state.update { it.copy(objectiveDifficulty = aiDiff, isAiThinking = false) }
                },
                onFailure = {
                    _state.update {
                        it.copy(
                            objectiveDifficulty = AchievementDifficulty.NONE,
                            isAiThinking = false
                        )
                    }
                }
            )
        }
    }

    private fun loadGameChallenges(gameId: Int) {
        viewModelScope.launch {
            getChallengesForGameUseCase(gameId).fold(
                onSuccess = { challengesList ->
                    _state.update { it.copy(customChallenges = challengesList) }
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message) }
                }
            )
        }
    }

    private fun loadCheapShark(gameName: String) {
        viewModelScope.launch {
            val price = getBestGameDealUseCase(gameName)
            if (price != null) {
                _state.update { it.copy(cheapestPrice = price) }
            }
        }
    }
}
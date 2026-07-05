package com.vdggrtf.playlog.presentation.main.game_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.network.api.CheapSharkApi
import com.vdggrtf.playlog.data.network.dto.AchievementDto
import com.vdggrtf.playlog.data.network.dto.CashedGameDto
import com.vdggrtf.playlog.data.network.dto.SupabaseGameDto
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.AiRepository
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    val isChallengeVerifying: Boolean = false
)


@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val libraryRepository: LibraryRepository,
    private val aiRepository: AiRepository,
    savedStateHandle: SavedStateHandle,
    private val cheapSharkApi: CheapSharkApi,
    private val supabase: SupabaseClient,
) : ViewModel() {

    private val _state = MutableStateFlow(GameDetailsState())
    val state = _state.asStateFlow()

    init {
        val gameId = savedStateHandle.get<Int>("gameId") ?: -1
        if (gameId != -1) {
            loadGameDetails(gameId)
            checkIfGameInMyLibrary(gameId)
            loadCommunityRating(gameId)
        } else {
            _state.update { it.copy(isLoading = false, error = "Invalid game ID") }
        }
    }

    private fun loadGameDetails(id: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null, isAiThinking = true) }

                //(Offline-First)
                val localGame = libraryRepository.getLocalGameById(id)
                if (localGame != null) {
                    _state.update {
                        it.copy(
                            game = localGame,
                            // Ai difficulty from Room
                            objectiveDifficulty = localGame.aiDifficulty
                        )
                    }
                }

                // Network requests
                val cachedDto = gameRepository.getCachedGame(id)
                if (cachedDto != null) {
                    Log.d("RAWG_CACHE", "🔥 HIT! ВЗЯЛИ ВСЁ ИЗ КЭША! RAWG СЭКОНОМЛЕН НА 100%!")

                    val cachedGameModel = cachedDto.toDomainModel()
                    val aiDiff = cachedDto.aiDifficulty?.let {
                        try {
                            AchievementDifficulty.valueOf(it)
                        } catch (e: Exception) {
                            AchievementDifficulty.NONE
                        }
                    } ?: AchievementDifficulty.NONE

                    // SMART MERGE #1 (FOR CACHE)
                    val finalGame = cachedGameModel.copy(
                        status = localGame?.status ?: GameStatus.NONE,
                        aiDifficulty = localGame?.aiDifficulty ?: aiDiff,
                        userDifficulty = localGame?.userDifficulty ?: AchievementDifficulty.NONE,
                        verifiedDifficulty = localGame?.verifiedDifficulty
                            ?: AchievementDifficulty.NONE
                    )

                    _state.update {
                        it.copy(
                            game = finalGame,
                            screenshots = cachedDto.screenshots ?: emptyList(),
                            achievements = cachedDto.achievements ?: emptyList(),
                            objectiveDifficulty = localGame?.aiDifficulty ?: aiDiff,
                            isAiThinking = false,
                            isSavedLibrary = localGame != null
                        )
                    }

                    // If the AI hasn't evaluated it yet, we'll nudge it.
                    if (aiDiff == AchievementDifficulty.NONE && localGame?.aiDifficulty == AchievementDifficulty.NONE) {
                        launch {
                            try {
                                val newAiDiff = aiRepository.evaluateGameDifficulty(finalGame.name)
                                _state.update {
                                    it.copy(
                                        objectiveDifficulty = newAiDiff,
                                        isAiThinking = false
                                    )
                                }
                                if (newAiDiff != AchievementDifficulty.NONE) {
                                    gameRepository.saveToCache(cachedDto.copy(aiDifficulty = newAiDiff.name))
                                }
                            } catch (e: Exception) {
                                _state.update { it.copy(isAiThinking = false) }
                            }
                        }
                    }

                    loadCheapShark(finalGame.name)

                    return@launch
                }

                Log.d("RAWG_CACHE", "🧊 MISS! Кэша нет. Качаем из RAWG...")
                coroutineScope {
                    // We are running everything in parallel.
                    val detailsDef = async { gameRepository.getGameDetails(id) }
                    val screensDef = async { gameRepository.getScreenshots(id) }
                    val achivDef = async { gameRepository.getGameAchievements(id) }

                    val detailsResult = detailsDef.await()
                    val screens = screensDef.await().getOrNull() ?: emptyList()
                    val achivs = achivDef.await().getOrNull() ?: emptyList()

                    if (detailsResult.isSuccess) {
                        val networkGame = detailsResult.getOrNull()!!

                        //  SMART MERGE #2 (FOR RAWG)
                        val mergedGame = networkGame.copy(
                            status = localGame?.status ?: GameStatus.NONE,
                            aiDifficulty = localGame?.aiDifficulty ?: AchievementDifficulty.NONE,
                            userDifficulty = localGame?.userDifficulty
                                ?: AchievementDifficulty.NONE,
                            verifiedDifficulty = localGame?.verifiedDifficulty
                                ?: AchievementDifficulty.NONE
                        )

                        _state.update {
                            it.copy(
                                game = mergedGame,
                                screenshots = screens,
                                achievements = achivs,
                                isSavedLibrary = localGame != null
                            )
                        }

                        loadCheapShark(mergedGame.name)

                        launch {
                            try {
                                val aiDiff = aiRepository.evaluateGameDifficulty(networkGame.name)
                                _state.update {
                                    it.copy(
                                        objectiveDifficulty = aiDiff,
                                        isAiThinking = false
                                    )
                                }

                                val cacheDto = CashedGameDto(
                                    id = networkGame.id,
                                    name = networkGame.name,
                                    imageUrl = networkGame.imageUrl,
                                    releasedDate = networkGame.releasedDate,
                                    rating = networkGame.rating,
                                    description = networkGame.descriptionRaw,
                                    screenshots = screens,
                                    achievements = achivs,
                                    aiDifficulty = if (aiDiff == AchievementDifficulty.NONE) null else aiDiff.name
                                )
                                gameRepository.saveToCache(cacheDto)
                            } catch (e: Exception) {
                                _state.update { it.copy(isAiThinking = false) }
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Ошибка сети: ${detailsResult.exceptionOrNull()?.message}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GameDetailsVM", "🚨 ФАТАЛЬНАЯ ОШИБКА: ${e.stackTraceToString()}")
                _state.update { it.copy(error = "Сбой загрузки данных: ${e.message}") }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkIfGameInMyLibrary(id: Int) {
        viewModelScope.launch {
            libraryRepository.getMyLibrary().collect { library ->
                val savedGame = library.find { it.id == id }
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
        val currentState = _state.value
        val currentGame = _state.value.game ?: return

        viewModelScope.launch {
            if (newStatus == GameStatus.NONE) {
                libraryRepository.deleteGameFromLibrary(currentGame)
                _state.update {
                    it.copy(
                        isSavedLibrary = false,
                        currentGameStatus = GameStatus.NONE
                    )
                }
            } else {
                // USER JUST ADDED THE GAME. THEY HAVEN'T PROVEN 100% YET. SETTING IT TO NONE!
                val updateGame = currentGame.copy(
                    status = newStatus,
                    aiDifficulty = currentState.objectiveDifficulty,
                    userDifficulty = currentGame.userDifficulty,
                    verifiedDifficulty = currentGame.verifiedDifficulty
                )
                libraryRepository.addGameToLibrary(updateGame)

                _state.update {
                    it.copy(isSavedLibrary = true, currentGameStatus = newStatus, game = updateGame)
                }
            }
        }
    }

    fun completeGameWithUserRating(userDiff: AchievementDifficulty) {
        val currentGame = _state.value.game ?: return
        val aiDiff = _state.value.objectiveDifficulty

        viewModelScope.launch {
            // GAME OBJECT
            val completedGame = currentGame.copy(
                status = GameStatus.COMPLETED, // Saving the completion status.
                verifiedDifficulty = aiDiff,
                userDifficulty = userDiff
            )

            libraryRepository.addGameToLibrary(completedGame) // Sending to Room and Supabase.

            // Update UI
            _state.update {
                it.copy(
                    game = completedGame,
                    isSavedLibrary = true,
                    currentGameStatus = GameStatus.COMPLETED
                )
            }
            Log.d("GameDetailsVM", "✅ Игра пройдена! ИИ: ${aiDiff.name}, Юзер: ${userDiff.name}")

            loadCommunityRating(completedGame.id)
        }
    }

    private fun loadCommunityRating(gameId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Downloading ALL entries for this game from all users' libraries,
                // where the user has actually provided a rating (not NONE)
                val votes = supabase.from("games_library")
                    .select {
                        filter {
                            eq("game_id_rawg", gameId)
                            neq("user_difficulty", "NONE")
                        }
                    }.decodeList<SupabaseGameDto>()

                if (votes.isNotEmpty()) {
                    // Find most popular difficulty
                    val popularVoteString = votes
                        .groupingBy { it.userDifficulty }
                        .eachCount()
                        .maxByOrNull { it.value }?.key

                    val commDiff = try {
                        AchievementDifficulty.valueOf(popularVoteString ?: "NONE")
                    } catch (e: Exception) {
                        AchievementDifficulty.NONE
                    }

                    _state.update {
                        it.copy(
                            communityDifficulty = commDiff,
                            communityVotesCount = votes.size
                        )
                    }
                    Log.d(
                        "GameDetailsVM",
                        "🔥 Мнение комьюнити: ${commDiff.name} (Голосов: ${votes.size})"
                    )
                }
            } catch (e: Exception) {
                Log.e("GameDetailsVM", "Ошибка загрузки комьюнити-рейтинга: ${e.message}")
            }
        }
    }

    fun retryAiEvaluation() {
        val game = _state.value.game ?: return

        viewModelScope.launch {
            // Progress Circle (Ai thinking)
            _state.update { it.copy(isAiThinking = true) }

            try {
                Log.d("GameDetailsVM", "🔄 Пинаем ИИ заново для игры ${game.name}...")
                val aiDiff = aiRepository.evaluateGameDifficulty(game.name)

                // Update UI
                _state.update { it.copy(objectiveDifficulty = aiDiff, isAiThinking = false) }

                // If the AI provided a valid difficulty - we update the global cache!
                if (aiDiff != AchievementDifficulty.NONE) {
                    val cached = gameRepository.getCachedGame(game.id)
                    if (cached != null) {
                        gameRepository.saveToCache(cached.copy(aiDifficulty = aiDiff.name))
                        Log.d("RAWG_CACHE", "✅ Успешно обновили сложность в кэше на ${aiDiff.name}")
                    }
                }
            } catch (e: Exception) {
                Log.e("GameDetailsVM", "❌ ИИ снова упал: ${e.message}")
                _state.update {
                    it.copy(
                        objectiveDifficulty = AchievementDifficulty.NONE,
                        isAiThinking = false
                    )
                }
            }
        }
    }

    private fun loadCheapShark(gameName: String) {
        viewModelScope.launch {
            try {
                val dealsResponse = cheapSharkApi.getStoreSpecificDeals(gameName)
                if (dealsResponse.isSuccessful) {
                    val deals = dealsResponse.body()
                    if (!deals.isNullOrEmpty()) {
                        // 🔥 СЕНЬОРСКИЙ ФИЛЬТР:
                        // Ищем точное совпадение по имени ИЛИ отсеиваем слова DLC, Pass, Upgrade, Expansion
                        val exactDeal = deals.firstOrNull { deal ->
                            deal.title.equals(gameName, ignoreCase = true)
                        } ?: deals.firstOrNull { deal ->
                            !deal.title.contains("DLC", ignoreCase = true) &&
                                    !deal.title.contains("Pass", ignoreCase = true) &&
                                    !deal.title.contains("Upgrade", ignoreCase = true) &&
                                    !deal.title.contains("Expansion", ignoreCase = true)
                        } ?: deals.first() // Если ничего не подошло, берем первый (fallback)

                        _state.update { it.copy(cheapestPrice = exactDeal.salePrice) }
                        Log.d("SHARK", "Filtered deal: ${exactDeal.title} for $${exactDeal.salePrice}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SHARK", "Акула не ответила: ${e.message}")
            }
        }
    }
}
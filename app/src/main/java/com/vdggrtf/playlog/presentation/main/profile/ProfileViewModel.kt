package com.vdggrtf.playlog.presentation.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.usecase.challenge.GetCustomChallengesUseCase
import com.vdggrtf.playlog.domain.usecase.library.GetCompletedBountiesCountUseCase
import com.vdggrtf.playlog.domain.usecase.profile.GetTotalBountyXpUseCase
import com.vdggrtf.playlog.domain.usecase.profile.LogoutUseCase
import com.vdggrtf.playlog.domain.usecase.profile.ObserveCachedUserUseCase
import com.vdggrtf.playlog.domain.usecase.profile.ObserveProfileStatsUseCase
import com.vdggrtf.playlog.domain.usecase.profile.SyncUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val email: String = "",
    val name: String = "",
    val avatarUrl: String? = null,
    val isPremiumChad: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val hallOfFameStats: Map<String, Int> = emptyMap(),
    val totalGames: Int = 0,
    val completedGames: Int = 0,
    val customChallengeCount: Int = 0,
    val favDifficulty: String = "N/A",
    val totalBounty: Int = 0,
)


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeCachedUserUseCase: ObserveCachedUserUseCase,
    private val syncUserProfileUseCase: SyncUserProfileUseCase,
    private val observeProfileStatsUseCase: ObserveProfileStatsUseCase,
    private val getTotalBountyXpUseCase: GetTotalBountyXpUseCase,
    private val getCompletedBountiesCountUseCase: GetCompletedBountiesCountUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        // 1. Подписываемся на локальное имя/email
        viewModelScope.launch {
            observeCachedUserUseCase().collect { (name, email) ->
                _state.update { it.copy(name = name, email = email) }
            }
        }

        // 2. Синхронизируем облако в фоне
        // In parallel, we are attempting to update data from the cloud if there is a network connection.
        viewModelScope.launch {
            syncUserProfileUseCase()
        }

        // 3. Подписываемся на статистику Библиотеки
        // Starting statistics collection
        viewModelScope.launch {
            observeProfileStatsUseCase().collect { stats ->
                _state.update {
                    it.copy(
                        totalGames = stats.totalGames,
                        completedGames = stats.completedGames,
                        customChallengeCount = stats.customChallengeCount,
                        favDifficulty = stats.peakDifficulty
                    )
                }
            }
        }


        calculateTotalBounty()
    }

    private fun calculateTotalBounty() {
        viewModelScope.launch {
            // Fetching XP!
            val total = getTotalBountyXpUseCase()
            // 💥 Take real count completed challenges to SUPABASE:
            val completedChallengesCount = getCompletedBountiesCountUseCase()

            _state.update { currentState ->

                val actualPeak = if (completedChallengesCount > 0) {
                    AchievementDifficulty.CUSTOM_CHALLENGE.title.uppercase()
                } else {
                    currentState.favDifficulty
                }

                currentState.copy(
                    totalBounty = total,
                    customChallengeCount = completedChallengesCount, // 💥 ОБНОВЛЯЕМ ПАСПОРТ!
                    favDifficulty = actualPeak
                )
            }
        }
    }

    fun buyPremiumChadStatus() {
        viewModelScope.launch {
            // Имитация покупки. В будущем тут будет запрос к Google Billing
            _state.update { it.copy(isLoading = true) }
            delay(1000)
            _state.update { it.copy(isPremiumChad = true, isLoading = false) }

        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            logoutUseCase()
            _state.update { it.copy(isLoading = false, isLoggedOut = true) }
        }
    }

    fun resetLogoutState() {
        _state.update { it.copy(isLoggedOut = false) }
    }
}
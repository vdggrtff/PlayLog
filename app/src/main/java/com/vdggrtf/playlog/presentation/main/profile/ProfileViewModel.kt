package com.vdggrtf.playlog.presentation.main.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.data.local.datastore.UserStorage
import com.vdggrtf.playlog.domain.model.AchievementDifficulty
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
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
    val mythicalCount: Int = 0,
    val favDifficulty: String = "N/A"
)


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val userStorage: UserStorage,
    private val repository: LibraryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        // (Offline-First)
        observeLocalStorage()

        // In parallel, we are attempting to update data from the cloud if there is a network connection.
        refreshProfileFromCloud()

        // Starting statistics collection
        observeLibraryStats()
    }

    private fun observeLocalStorage() {
        viewModelScope.launch {
            // Collecting name from DataStore
            userStorage.userName.collect { name ->
                _state.update { it.copy(name = name) }
            }
        }
        viewModelScope.launch {
            // Collecting email from DataStore
            userStorage.userEmail.collect { email ->
                _state.update { it.copy(email = email) }
            }
        }
    }

    private fun refreshProfileFromCloud() {
        val user = supabase.auth.currentUserOrNull()
        if (user != null) {
            // Removed ?: "Gamer"
            val cloudName = user.userMetadata?.get("name")?.toString()?.replace("\"", "")
            val userEmail = user.email ?: ""

            viewModelScope.launch {
                // Updating the cache ONLY if a real name was received from the cloud
                if (!cloudName.isNullOrBlank()) {
                    userStorage.saveUserData(cloudName, userEmail)
                }
            }
        }
    }

    private fun observeLibraryStats(){
        viewModelScope.launch {
            repository.getMyLibrary().collect {games ->

                val total = games.size

                val completed = games.count { it.status == GameStatus.COMPLETED }

                val custom = games.count { it.verifiedDifficulty == AchievementDifficulty.CUSTOM_CHALLENGE }

                val peakDiff = games
                    .filter { it.status == GameStatus.COMPLETED && it.verifiedDifficulty != AchievementDifficulty.NONE }
                    .maxByOrNull { it.verifiedDifficulty.ordinal }
                    ?.verifiedDifficulty?.title ?: "N/A"

                _state.update { it.copy(
                    totalGames = total,
                    completedGames = completed,
                    mythicalCount = custom,
                    favDifficulty = peakDiff
                ) }
            }
        }
    }

    fun buyPremiumChadStatus() {
        viewModelScope.launch {
            // Имитация покупки. В будущем тут будет запрос к Google Billing
            _state.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000) // Пафосная задержка "обработки транзакции"

            _state.update { it.copy(isPremiumChad = true, isLoading = false) }

        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                supabase.auth.signOut()
                userStorage.clearStorage()
                repository.clearLocalDatabase()
                _state.update { it.copy(isLoggedOut = true, isLoading = false) }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Ошибка при выходе: ${e.message}")
                userStorage.clearStorage()
                repository.clearLocalDatabase()
                _state.update { it.copy(isLoggedOut = true, isLoading = false) }
            }
        }
    }

    fun resetLogoutState() {
        _state.update { it.copy(isLoggedOut = false) }
    }
}
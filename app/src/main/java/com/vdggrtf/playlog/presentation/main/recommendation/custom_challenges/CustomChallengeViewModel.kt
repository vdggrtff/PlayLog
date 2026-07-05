package com.vdggrtf.playlog.presentation.main.recommendation.custom_challenges

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.network.dto.ChallengeDto
import com.vdggrtf.playlog.data.network.dto.ChallengeStatusResponseDto
import com.vdggrtf.playlog.data.network.dto.ChallengeStatusUpdateDto
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChallengeBoardState(
    val isLoading: Boolean = false,
    val challenges: List<CustomChallengeModel> = emptyList(),
    val error: String? = null,
    val isVerifying: Boolean = false, // AI is thinking
    val successMessage: String? = null, // AI approved
)

@HiltViewModel
class ChallengeBoardViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val aiRepository: AiRepository, // Inject AI Repository!
) : ViewModel() {

    private val _state = MutableStateFlow(ChallengeBoardState())
    val state = _state.asStateFlow()

    init {
        fetchChallenges()
    }

    private fun fetchChallenges() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // 1. Fetching all contracts
                val dtos = supabase.from("custom_challenge")
                    .select()
                    .decodeList<ChallengeDto>()

                // 2. Fetching all user statuses using the DTO from your data layer!
                val userStatuses = try {
                    supabase.from("user_challenge_status")
                        .select(columns = Columns.list("challenge_id, status"))
                        .decodeList<ChallengeStatusResponseDto>()
                } catch (e: Exception) {
                    Log.e("BOUNTY", "Failed to fetch user statuses: ${e.message}")
                    emptyList()
                }

                // 3. Merging them into Domain Models
                val domainModels = dtos.map { dto ->
                    val domainModel = dto.toDomainModel()

                    // Finding saved status for this specific challenge
                    val savedStatusStr = userStatuses.find { it.challengeId == dto.id }?.status

                    val gameStatus = try {
                        if (savedStatusStr != null) GameStatus.valueOf(savedStatusStr) else GameStatus.NONE
                    } catch (e: Exception) {
                        GameStatus.NONE
                    }

                    // Updating the model
                    domainModel.copy(
                        status = gameStatus,
                        isCompleted = gameStatus == GameStatus.COMPLETED
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        challenges = domainModels,
                        error = null
                    )
                }
                Log.d("BOUNTY_BOARD", "Loaded ${domainModels.size} challenges!")
            } catch (e: Exception) {
                Log.e("BOUNTY_BOARD", "Error fetching challenges: ${e.message}")
                _state.update {
                    it.copy(isLoading = false, error = "Failed to load bounties.")
                }
            }
        }
    }

    // 3. THE CORE FUNCTION: AI Verification -> Supabase Insert
    fun verifyChallengeProof(challenge: CustomChallengeModel, imageBytes: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(isVerifying = true, error = null, successMessage = null) }

            try {
                Log.i("BOUNTY", "Sending proof to Gemini for ${challenge.title}...")

                // 1. Ask Gemini
                val isVerified = aiRepository.verifyCustomChallenge(
                    imageBytes = imageBytes,
                    gameName = challenge.title, // Pass title for context
                    challengePrompt = challenge.aiPrompt
                )

                if (isVerified) {
                    Log.d("BOUNTY", "Gemini APPROVED! Saving to Supabase...")

                    // 2. Save to Supabase table
                    updateChallengeStatus(challenge.id, GameStatus.COMPLETED)

                    // 3. Update local state so UI shows completed
                    _state.update {
                        it.copy(
                            isVerifying = false,
                            successMessage = "Contract Completed! You earned ${challenge.rewardPoints} XP."
                        )
                    }
                } else {
                    Log.w("BOUNTY", "Gemini REJECTED the proof.")
                    _state.update {
                        it.copy(
                            isVerifying = false,
                            error = "Proof rejected! Ensure your screenshot clearly shows the required condition."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BOUNTY", "Fatal Error: ${e.message}")
                _state.update {
                    it.copy(
                        isVerifying = false,
                        error = "Network or AI Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateChallengeStatus(challengeId: Int, newStatus: GameStatus) {
        viewModelScope.launch {
            try {
                if (newStatus == GameStatus.NONE) {
                    // Deleting the record if user removes the bounty from library
                    supabase.from("user_challenge_status")
                        .delete { filter { eq("challenge_id", challengeId) } }
                } else {
                    // Creating payload using the DTO from your data layer
                    val payload = ChallengeStatusUpdateDto(
                        challengeId = challengeId,
                        status = newStatus.name
                    )

                    // Safely deleting old status before inserting new one to prevent duplicates
                    supabase.from("user_challenge_status")
                        .delete { filter { eq("challenge_id", challengeId) } }

                    // Inserting new status
                    supabase.from("user_challenge_status").insert(payload)
                }

                // Updating local UI state
                val updatedList = _state.value.challenges.map {
                    if (it.id == challengeId) {
                        it.copy(
                            status = newStatus,
                            isCompleted = newStatus == GameStatus.COMPLETED
                        )
                    } else it
                }

                _state.update { it.copy(challenges = updatedList) }

            } catch (e: Exception) {
                Log.e("BOUNTY", "Error updating status: ${e.message}")
            }
        }
    }

    fun refreshChallenges() {
        fetchChallenges()
    }

    fun clearAlerts() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
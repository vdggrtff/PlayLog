package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.network.dto.supabase.ChallengeDto
import com.vdggrtf.playlog.data.network.dto.supabase.ChallengeGameIdDto
import com.vdggrtf.playlog.data.network.dto.supabase.ChallengeStatusResponseDto
import com.vdggrtf.playlog.data.network.dto.supabase.ChallengeStatusUpdateDto
import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus
import com.vdggrtf.playlog.domain.repository.ChallengeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ChallengeRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : ChallengeRepository {

    override suspend fun getChallenges(): Result<List<CustomChallengeModel>> {
        return try {
            // 1. Fetching all contracts
            val dtos = supabase.from("custom_challenge")
                .select()
                .decodeList<ChallengeDto>()

            // Map DTOs to Domain models (assuming toDomainModel() is imported)
            val models = dtos.map { it.toDomainModel() }
            Result.success(models)
        } catch (e: Exception) {
            Log.e("ChallengeRepository", "Error fetching challenges: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getCompletedChallengeIds(): Result<List<Int>> {
        return try {
            val completedRecords = supabase.from("user_challenge_status")
                .select(columns = Columns.list("challenge_id")) {
                    filter {
                        eq("status", "COMPLETED")
                    }
                }.decodeList<ChallengeStatusResponseDto>()

            val ids = completedRecords.map { it.challengeId }
            Result.success(ids)
        } catch (e: Exception){
            Log.e("ChallengeRepository", "Error fetching completed IDs: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateChallengeStatus(
        challengeId: Int,
        newStatus: GameStatus,
    ): Result<Unit> {
        return try {
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
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ChallengeRepository", "Error updating challenge status: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUserChallengeStatuses(): Result<Map<Int, GameStatus>> {
        return try {
            val records = supabase.from("user_challenge_status")
                .select(columns = Columns.list("challenge_id, status"))
                .decodeList<ChallengeStatusResponseDto>()

            // Map list of DTOs into a clean Kotlin Map
            val statusMap = records.associate { dto ->
                val gameStatus = try {
                    GameStatus.valueOf(dto.status)
                } catch (e: Exception) {
                    GameStatus.NONE
                }
                dto.challengeId to gameStatus
            }
            Result.success(statusMap)
        } catch (e: Exception) {
            Log.e("ChallengeRepository", "Error fetching challenge statuses: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getChallengesByGameId(gameId: Int): Result<List<CustomChallengeModel>> {
        return try {
            val dtos = supabase.from("custom_challenge")
                .select { filter { eq("game_id", gameId) } }
                .decodeList<ChallengeDto>()

            val models = dtos.map { it.toDomainModel() }
            Result.success(models)
        } catch (e: Exception){
            Log.e("ChallengeRepository", "Error downloading challenges games: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getTrackedBountyGameIds(): Result<Set<Int>> {
        return try {
            val userStatuses = supabase.from("user_challenge_status")
                .select( columns = Columns.list("challenge_id, status"))
                .decodeList<ChallengeStatusResponseDto>()

            val challengesIds = userStatuses.map { it.challengeId }
            if (challengesIds.isEmpty()) return Result.success(emptySet())

            val challenges = supabase.from("custom_challenge")
                .select(columns = Columns.list("id, game_id")){
                    filter { isIn("id", challengesIds) }
                }.decodeList<ChallengeGameIdDto>()

            val gamesId = challenges.map { it.gameId }.toSet()

            Result.success(gamesId)
        } catch (e: Exception){
            Log.e("ChallengeRepository", "Ошибка получения ID игр с контрактами: ${e.message}")
            Result.failure(e)
        }

    }
}
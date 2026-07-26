package com.vdggrtf.playlog.domain.repository

import com.vdggrtf.playlog.domain.model.CustomChallengeModel
import com.vdggrtf.playlog.domain.model.GameStatus

interface ChallengeRepository {
    // Fetches all available global challenges from Supabase
    suspend fun getChallenges(): Result<List<CustomChallengeModel>>

    // Fetches only completed challenge IDs for the current user
    suspend fun getCompletedChallengeIds(): Result<List<Int>>

    // Updates or deletes the challenge status in Supabase
    suspend fun updateChallengeStatus(challengeId: Int, newStatus: GameStatus): Result<Unit>

    // Fetches a map of challenge IDs to their current game statuses for the active user
    suspend fun getUserChallengeStatuses(): Result<Map<Int, GameStatus>>

    suspend fun getChallengesByGameId(gameId: Int): Result<List<CustomChallengeModel>>

    suspend fun getTrackedBountyGameIds(): Result<Set<Int>>
}
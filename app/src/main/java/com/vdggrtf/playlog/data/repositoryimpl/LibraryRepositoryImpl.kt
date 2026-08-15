package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.local.dao.GameDao
import com.vdggrtf.playlog.data.local.entity.GAME_DB_NAME
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.mapper.toEntity
import com.vdggrtf.playlog.data.mapper.toSupabaseDto
import com.vdggrtf.playlog.data.network.dto.supabase.challenges.BountyRewardDto
import com.vdggrtf.playlog.data.network.dto.supabase.challenges.CompletedBountyDto
import com.vdggrtf.playlog.data.network.dto.supabase.SupabaseGameDto
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val dao: GameDao,
    private val supabase: SupabaseClient,
) : LibraryRepository {


    override fun getMyLibrary(): Flow<List<GameModel>> {
        return dao.getMyLibrary().map { entities ->
            entities.map {
                it.toDomainModel()
            }
        }
    }

    override suspend fun addGameToLibrary(gameModel: GameModel) {
        withContext(Dispatchers.IO) {
            dao.addGame(gameModel.toEntity())
        }

        // cloud
        withContext(Dispatchers.IO) {
            try {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    val userId = session.user?.id ?: return@withContext

                    // step 1: checking if this game is already in the cloud
                    val existingGames = supabase.from(GAME_DB_NAME)
                        .select {
                            filter {
                                eq("user_id", userId)
                                eq("game_id_rawg", gameModel.id)
                            }
                        }.decodeList<SupabaseGameDto>()

                    if (existingGames.isNotEmpty()) {
                        // game exists! updating status, difficulties AND new marketplace fields!
                        supabase.from(GAME_DB_NAME).update(
                            {
                                set("status", gameModel.status.name)
                                set("ai_difficulty", gameModel.aiDifficulty.name)
                                set("user_difficulty", gameModel.userDifficulty.name)
                                set("verified_difficulty", gameModel.verifiedDifficulty.name)
                                // 💥 Updating genres just in case it was saved in an older version of the app!
                                set("genres", gameModel.genres.joinToString(","))
                                set("platforms", gameModel.platforms.joinToString(","))
                            }
                        ) {
                            filter {
                                eq("user_id", userId)
                                eq("game_id_rawg", gameModel.id)
                            }
                        }
                        Log.d(
                            "SupabaseSync",
                            "Игра обновлена в облаке! Verified: ${gameModel.verifiedDifficulty.name}"
                        )
                    } else {
                        // 💥 CLEAN ARCHITECTURE: Using our new mapper!
                        val newSupabaseGame = gameModel.toSupabaseDto(userId)
                        supabase.from(GAME_DB_NAME).insert(newSupabaseGame)
                        Log.d("SupabaseSync", "Новая игра ${gameModel.name} СОХРАНЕНА в облако!")
                    }
                }
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Ошибка облака: ${e.message}")
            }
        }
    }

    override suspend fun deleteGameFromLibrary(gameModel: GameModel) {
        withContext(Dispatchers.IO) {
            dao.deleteGame(gameModel.toEntity())
        }
    }

    override suspend fun getLocalGameById(id: Int): GameModel? {
        return withContext(Dispatchers.IO) {
            dao.getGameByIdSync(id)?.toDomainModel()
        }
    }

    override suspend fun getCommunityDifficulties(gameId: Int): Result<List<String>> {
        return try {
            val votes = supabase.from("games_library")
                .select {
                    filter {
                        eq("game_id_rawg", gameId)
                        neq("user_difficulty", "NONE")
                    }
                }.decodeList<SupabaseGameDto>()

            Result.success(votes.map { it.userDifficulty })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTotalBounty(): Int {
        return try {
            // Get IDs of completed challenges
            val completedRecords = supabase.from("user_challenge_status")
                .select(columns = Columns.list("challenge_id")) {
                    filter {
                        eq("status", "COMPLETED")
                    }
                }.decodeList<CompletedBountyDto>()

            val ids = completedRecords.map { it.challengeId }

            if (ids.isEmpty()) return 0

            // Fetch the actual reward points for these challenges
            // Supabase trick: using 'in' filter to get multiple rows by ID
            val rewards = supabase.from("custom_challenge")
                .select(columns = Columns.list("reward_points")) {
                    filter {
                        isIn("id", ids)
                    }
                }.decodeList<BountyRewardDto>()

            // Math time!
            val total = rewards.sumOf { it.rewardPoints }
            return total
        } catch (e: Exception) {
            Log.e("ProfileVM", "Ошибка подсчета Bounty: ${e.message}")
            0
        }
    }

    override fun getGamesForPlaylist(playlistId: String): Flow<List<GameModel>> {
        return dao.getGamesForPlaylist(playlistId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun clearLocalDatabase() {
        withContext(Dispatchers.IO) {
            try {
                dao.clearAllGames()
                Log.d("RoomDatabase", "Локальная база успешно очищена при выходе")
            } catch (e: Exception) {
                Log.e("RoomDatabase", "Ошибка очистки базы: ${e.message}")
            }
        }
    }
}
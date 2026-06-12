package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.local.dao.GameDao
import com.vdggrtf.playlog.data.local.entity.DB_NAME
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.mapper.toEntity
import com.vdggrtf.playlog.data.network.dto.SupabaseGameDto
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.LibraryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
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
                    val existingGames = supabase.from(DB_NAME)
                        .select {
                            filter {
                                eq("user_id", userId)
                                eq("game_id_rawg", gameModel.id)
                            }
                        }.decodeList<SupabaseGameDto>()

                    if (existingGames.isNotEmpty()) {
                        // game exists! updating status and our three difficulties!
                        supabase.from("games_library").update(
                            {
                                set("status", gameModel.status.name)
                                set("ai_difficulty", gameModel.aiDifficulty.name)
                                set("user_difficulty", gameModel.userDifficulty.name)
                                set("verified_difficulty", gameModel.verifiedDifficulty.name)
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
                        // no game! creating a new entry with all data
                        val newSupabaseGame = SupabaseGameDto(
                            userId = userId,
                            gameIdRawg = gameModel.id,
                            name = gameModel.name,
                            imageUrl = gameModel.imageUrl ?: "",
                            status = gameModel.status.name,
                            rating = gameModel.rating ?: 0.0,
                            releasedDate = gameModel.releasedDate ?: "",
                            description = gameModel.descriptionRaw ?: "",
                            aiDifficulty = gameModel.aiDifficulty.name,
                            userDifficulty = gameModel.userDifficulty.name,
                            verifiedDifficulty = gameModel.verifiedDifficulty.name
                        )
                        supabase.from(DB_NAME).insert(newSupabaseGame)
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
package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.network.api.CheapSharkApi
import com.vdggrtf.playlog.data.network.api.RawgApi
import com.vdggrtf.playlog.data.network.dto.AchievementDto
import com.vdggrtf.playlog.data.network.dto.CashedGameDto
import com.vdggrtf.playlog.data.network.dto.CheapSharkDealDto
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.utils.NetworkResult
import com.vdggrtf.playlog.utils.safeApiCall
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val api: RawgApi,
    private val supabase: SupabaseClient,
    private val cheapSharkApi: CheapSharkApi,
) : GameRepository {

    override suspend fun searchGames(query: String, page: Int): Result<List<GameModel>> {
        val result = safeApiCall {
            api.searchGames(query = query, page = page)
        }

        return when (result) {
            is NetworkResult.Success -> {
                // successfully downloaded 20 games, mapping them to domain models
                Result.success(result.data.results.map { it.toDomainModel() })
            }

            is NetworkResult.Error -> {
                Result.failure(Exception(result.message))
            }
        }
    }

    override suspend fun getPopularGames(page: Int): Result<List<GameModel>> {

        val result = safeApiCall {
            api.getPopularGames(page = page)
        }

        return when (result) {
            is NetworkResult.Success -> {
                // successfully downloaded 20 games, mapping them to domain models
                Result.success(result.data.results.map { it.toDomainModel() })
            }

            is NetworkResult.Error -> {
                Result.failure(Exception(result.message))
            }
        }
    }

    override suspend fun getGameDetails(id: Int): Result<GameModel> {
        return withContext(Dispatchers.IO) {
            val result = safeApiCall { api.getGameDetails(id) }
            when (result) {
                is NetworkResult.Success -> Result.success(result.data.toDomainModel())
                is NetworkResult.Error -> Result.failure(Exception(result.message))
            }
        }
    }

    override suspend fun getCachedGame(id: Int): CashedGameDto? {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("global_games_cache")
                    .select { filter { eq("id", id) } }
                    .decodeList<CashedGameDto>()
                    .firstOrNull()
            } catch (e: Exception) {
                // if the token has expired - attempting to refresh and retry.
                if (e.message?.contains("JWT expired") == true) {
                    Log.w("RAWG_CACHE", "⚠️ Токен протух при чтении! Обновляем...")
                    try {
                        supabase.auth.refreshCurrentSession()
                        return@withContext supabase.from("global_games_cache")
                            .select { filter { eq("id", id) } }
                            .decodeList<CashedGameDto>()
                            .firstOrNull()
                    } catch (refreshEx: Exception) {
                        Log.e("RAWG_CACHE", "❌ Не удалось обновить токен: ${refreshEx.message}")
                    }
                }
                Log.w("RAWG_CACHE", "Ошибка чтения кэша: ${e.message}")
                null
            }
        }
    }

    override suspend fun saveToCache(cacheDto: CashedGameDto) {
        withContext(Dispatchers.IO) {
            try {
                supabase.from("global_games_cache").upsert(cacheDto)
                Log.d("RAWG_CACHE", "💾 АБСОЛЮТНЫЙ КЭШ СОХРАНЕН!")
            } catch (e: Exception) {
                if (e.message?.contains("JWT expired") == true) {
                    Log.w("RAWG_CACHE", "⚠️ Токен протух при записи! Обновляем...")
                    try {
                        supabase.auth.refreshCurrentSession()
                        supabase.from("global_games_cache").upsert(cacheDto)
                        Log.d("RAWG_CACHE", "💾 АБСОЛЮТНЫЙ КЭШ СОХРАНЕН (со 2-й попытки)!")
                        return@withContext
                    } catch (refreshEx: Exception) {
                        // ignoring, if it didn't work, it didn't work.
                    }
                }
                Log.e("RAWG_CACHE", "Ошибка записи кэша: ${e.message}")
            }
        }
    }

    override suspend fun getScreenshots(id: Int): Result<List<String>> {


        val allImages = mutableListOf<String>()
        var currentPage = 1
        var hasNextPage = true

        while (hasNextPage && currentPage <= 5) {
            val result = safeApiCall {
                api.getScreenshots(
                    gameId = id,
                    currentPage
                )
            }

            when (result) {
                is NetworkResult.Success -> {
                    val urls = result.data.result.map { it.image }
                    allImages.addAll(urls)

                    if (result.data.next != null) {
                        currentPage++
                    } else {
                        hasNextPage = false
                    }
                }

                else -> {
                    hasNextPage = false
                }
            }
        }

        return Result.success(allImages)
    }

    override suspend fun getGameAchievements(id: Int): Result<List<AchievementDto>> {
        val allAchievement = mutableListOf<AchievementDto>()
        var currentPage = 1
        var hasNextPage = true

        while (hasNextPage && currentPage <= 5) {
            val result = safeApiCall {
                api.getGameAchievements(
                    gameId = id,
                    currentPage
                )
            }

            when (result) {
                is NetworkResult.Success -> {
                    allAchievement.addAll(result.data.results)

                    if (result.data.next != null) {
                        currentPage++
                    } else {
                        hasNextPage = false
                    }
                }

                else -> {
                    hasNextPage = false
                }
            }
        }



        return Result.success(allAchievement)
    }

    override suspend fun getGamePrices(gameName: String): Result<List<CheapSharkDealDto>> {
        return try {
            val response = cheapSharkApi.getStoreSpecificDeals(gameName)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error"))
            }
        } catch (e: Exception) {
            Log.e("SHARK", "Акула не ответила: ${e.message}")
            Result.failure(e)
        }
    }
}
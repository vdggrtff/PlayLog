package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.mapper.toDomainModel
import com.vdggrtf.playlog.data.network.api.CheapSharkApi
import com.vdggrtf.playlog.data.network.api.IgdbApi
import com.vdggrtf.playlog.data.network.api.RawgApi
import com.vdggrtf.playlog.data.network.dto.rawg.AchievementDto
import com.vdggrtf.playlog.data.network.dto.supabase.challenges.CashedGameDto
import com.vdggrtf.playlog.data.network.dto.cheapshark.CheapSharkDealDto
import com.vdggrtf.playlog.domain.model.GameModel
import com.vdggrtf.playlog.domain.repository.GameRepository
import com.vdggrtf.playlog.utils.NetworkResult
import com.vdggrtf.playlog.utils.safeApiCall
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.collections.map

class GameRepositoryImpl @Inject constructor(
    private val api: IgdbApi,
    private val supabase: SupabaseClient,
    private val cheapSharkApi: CheapSharkApi,
) : GameRepository {

    private val baseFields = "fields id, name, summary, rating, first_release_date, cover.image_id, genres.name, platforms.name;"

    override suspend fun searchGames(
        query: String,
        page: Int,
        dates: String?,
        genres: String?,
        platforms: String?,
    ): Result<List<GameModel>> {
        return try {
            val offset = (page - 1) * 40

            // В IGDB поиск работает невероятно круто через слово search!
            val apicalypseQuery = "search \"$query\"; $baseFields where cover != null; limit 40; offset $offset;"

            val requestBody = apicalypseQuery.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.getGames(requestBody)
            if (response.isSuccessful){
                val games = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.success(games)
            } else {
                Log.e("IGDB_ERROR", "Popular Error ${response.code()}: ${response.errorBody()?.string()}")
                Result.failure(Exception("IGDB API Error: ${response.code()}"))
            }
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getPopularGames(
        page: Int,
        dates: String?,
        genres: String?,
        platforms: String?,
    ): Result<List<GameModel>> {
        return try {
            val offset = (page - 1) * 40

            // Пишем запрос на языке Apicalypse!
            // Просим сортировать по количеству оценок (самые популярные) и брать только игры с обложками
            var filterClause = "cover != null & rating_count >= 10" // Только игры с обложкой и оценками
            if (!dates.isNullOrBlank()) filterClause += " & $dates"
            if (!genres.isNullOrBlank()) filterClause += " & genres = ($genres)"     // IGDB синтаксис: genres = (8,12)
            if (!platforms.isNullOrBlank()) filterClause += " & platforms = ($platforms)"

            val query = "$baseFields where $filterClause; sort rating_count desc; limit 40; offset $offset;"

            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.getGames(requestBody)

            Log.e("IGDB_ERROR", "Popular Error $query")
            Log.e("IGDB_ERROR", "Popular Error $requestBody")
            Log.e("IGDB_ERROR", "Popular Error $response")

            if (response.isSuccessful) {
                val games = response.body()?.map { it.toDomainModel() } ?: emptyList()
                Result.success(games)
            } else {
                // 💥 2. ПЕЧАТАЕМ ОШИБКУ В КОНСОЛЬ!
                Log.e("IGDB_ERROR", "Popular Error ${response.code()}: ${response.errorBody()?.string()}")
                Result.failure(Exception("IGDB API Error: ${response.code()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getGameDetails(gameId: Int): Result<GameModel> {
        return try {
            val query = "$baseFields fields screenshots.image_id; where id = $gameId;"

            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.getGames(requestBody)
            if (response.isSuccessful){
                val gameDto = response.body()?.firstOrNull()
                if (gameDto != null){
                    Result.success(gameDto.toDomainModel())
                }else {
                    Result.failure(Exception("Game not found"))
                }
            } else {
                Result.failure(Exception("IGDB Details Error"))
            }
        }catch (e: Exception){
            Result.failure(e)
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
        return try {
            val query = "fields screenshots.image_id; where id = $id;"
            val requestBody = query.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = api.getGames(requestBody)

            if (response.isSuccessful) {
                val gameDto = response.body()?.firstOrNull()
                // Мапим ID скриншотов в готовые URL 1080p!
                val screenUrls = gameDto?.screenshots?.mapNotNull {
                    it.imageId?.let { id -> "https://images.igdb.com/igdb/image/upload/t_1080p/$id.jpg" }
                } ?: emptyList()

                Result.success(screenUrls)
            } else {
                Result.failure(Exception("Screenshots error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGameAchievements(id: Int): Result<List<AchievementDto>> {
        return Result.success(emptyList())
        /*val allAchievement = mutableListOf<AchievementDto>()
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



        return Result.success(allAchievement)*/
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
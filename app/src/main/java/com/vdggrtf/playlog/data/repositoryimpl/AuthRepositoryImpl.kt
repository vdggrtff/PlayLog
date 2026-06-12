package com.vdggrtf.playlog.data.repositoryimpl

import android.util.Log
import com.vdggrtf.playlog.data.local.dao.GameDao
import com.vdggrtf.playlog.data.local.datastore.UserStorage
import com.vdggrtf.playlog.data.local.entity.DB_NAME
import com.vdggrtf.playlog.data.local.entity.GameEntity
import com.vdggrtf.playlog.data.network.dto.SupabaseGameDto
import com.vdggrtf.playlog.data.network.dto.UserProfileDto
import com.vdggrtf.playlog.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.MDC.put
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
    private val userStorage: UserStorage,
    private val dao: GameDao,
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                //  get session
                val session = supabase.auth.currentSessionOrNull()
                val user = session?.user

                if (session != null && user != null) {
                    val userEmail = user.email ?: ""
                    var userName = "Gamer"

                    // reading the profiles table name!
                    try {
                        val profile = supabase.from("profiles")
                            .select { filter { eq("id", user.id) } }
                            .decodeSingleOrNull<UserProfileDto>()

                        if (profile != null) {
                            userName = profile.userName
                        }
                    } catch (e: Exception) {
                        Log.e("Auth", "Не удалось скачать профиль: ${e.message}")
                    }

                    userStorage.saveToken(session.accessToken, session.refreshToken ?: "")
                    userStorage.saveUserData(userName, userEmail)

                    Log.i("SupabaseSync", "Clear old db and load new data...")
                    dao.clearAllGames()

                    val cloudGames = supabase.from(DB_NAME)
                        .select { filter { eq("user_id", user.id) } }
                        .decodeList<SupabaseGameDto>()

                    val entities = cloudGames.map { dto ->
                        GameEntity(
                            id = dto.gameIdRawg,
                            name = dto.name,
                            releasedDate = dto.releasedDate ?: "",
                            imageUrl = dto.imageUrl ?: "",
                            rating = dto.rating ?: 0.0,
                            status = dto.status,
                            unlockedAchievements = 0,
                            totalAchievements = 0,
                            descriptionRaw = dto.description,
                            playtime = 0,
                            aiDifficulty = dto.aiDifficulty,
                            userDifficulty = dto.userDifficulty,
                            verifiedDifficulty = dto.verifiedDifficulty
                        )
                    }
                    dao.insertAll(entities)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("Auth", "Ошибка логина: ${e.message}")
                Result.failure(e)
            }
        }
    }

    override suspend fun registerUser(
        email: String,
        password: String,
        name: String,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("Auth", "Начало регистрации -> Email: $email | Имя: '$name'")

                // reg user
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = buildJsonObject {
                        put("name", name)
                        put("full_name", name)
                    }
                }

                // save user data in storage
                userStorage.saveUserData(name, email)

                // saving tokens if the session was successfully established
                val session = supabase.auth.currentSessionOrNull()
                val user = session?.user

                if (session != null && user != null) {
                    val userEmail = user.email ?: ""

                    try {
                        val newProfile = UserProfileDto(
                            id = user.id,
                            userName = name
                        )
                        supabase.from("profiles").insert(newProfile)
                    } catch (e: Exception) {
                        Log.e("Auth", "Не удалось создать профиль в БД: ${e.message}")
                    }

                    userStorage.saveToken(session.accessToken, session.refreshToken ?: "")
                    userStorage.saveUserData(name, userEmail)
                    dao.clearAllGames()
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("Auth", "Ошибка регистрации: ${e.message}")
                Result.failure(e)
            }
        }
    }

    override suspend fun isUserLogIn(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.refreshCurrentSession()
                true
            } catch (e: Exception) {
                supabase.auth.currentSessionOrNull() != null
            }
        }
    }

}
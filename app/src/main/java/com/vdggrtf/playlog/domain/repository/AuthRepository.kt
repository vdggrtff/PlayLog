package com.vdggrtf.playlog.domain.repository

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun registerUser(email: String, password: String, name: String): Result<Unit>

    suspend fun isUserLogIn(): Boolean

    suspend fun isUserSessionActive(): Boolean
}
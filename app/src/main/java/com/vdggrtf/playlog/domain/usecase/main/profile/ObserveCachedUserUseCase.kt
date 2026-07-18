package com.vdggrtf.playlog.domain.usecase.main.profile

import com.vdggrtf.playlog.data.local.datastore.UserStorage
import io.ktor.util.Hash.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveCachedUserUseCase @Inject constructor(
    private val userStorage: UserStorage
) {

    suspend operator fun invoke(): Flow<Pair<String, String>>{
        return combine(userStorage.userName, userStorage.userEmail) {name, email ->
            Pair(name, email)
        }
    }
}
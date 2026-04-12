package com.github.rodionk77.feature.profile.data

import com.github.rodionk77.common.TokenStorage
import com.github.rodionk77.common.database.AppDatabase
import com.github.rodionk77.common.models.UserEntity
import com.github.rodionk77.feature.profile.data.models.GitHubEventEntity
import com.github.rodionk77.feature.profile.domain.ProfileRepository
import com.github.rodionk77.feature.repos.data.room.UserDao
import com.github.rodionk77.feature.repos.data.room.toDbEntity
import com.github.rodionk77.feature.repos.data.room.toDomainEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException

class ProfileRepositoryImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
    private val userDao: UserDao,
    private val database: AppDatabase
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserEntity> {
        return runCatching {
            val response = httpClient.get("user")
            val user: UserEntity = response.body()
            userDao.upsertUser(user.toDbEntity())
            user
        }.recoverCatching { e ->
            userDao.getUser()?.toDomainEntity() ?: throw e
        }.onFailure { if (it is CancellationException) throw it }
    }

    override suspend fun getCachedProfile(): UserEntity? {
        return userDao.getUser()?.toDomainEntity()
    }

    override suspend fun getEvents(username: String, page: Int, perPage: Int): Result<List<GitHubEventEntity>> {
        return runCatching {
            val response = httpClient.get("users/$username/events") {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
            response.body<List<GitHubEventEntity>>()
        }.onFailure { if (it is CancellationException) throw it }
    }

    override suspend fun logout() {
        tokenStorage.clearAll()
        database.clearAllData()
    }
}

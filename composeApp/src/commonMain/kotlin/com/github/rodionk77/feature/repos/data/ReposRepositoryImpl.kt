package com.github.rodionk77.feature.repos.data

import com.github.rodionk77.common.models.RepoEntity
import com.github.rodionk77.common.models.UserEntity
import com.github.rodionk77.feature.repos.data.room.ReposDao
import com.github.rodionk77.feature.repos.data.room.UserDao
import com.github.rodionk77.feature.repos.data.room.toDbEntity
import com.github.rodionk77.feature.repos.data.room.toDomainEntity
import com.github.rodionk77.feature.repos.domain.ReposRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException

class ReposRepositoryImpl(
    private val httpClient: HttpClient,
    private val reposDao: ReposDao,
    private val userDao: UserDao
) : ReposRepository {

    override suspend fun getRepositories(page: Int, perPage: Int, useCache: Boolean): Result<List<RepoEntity>> {
        if (useCache && page == 1) {
            val cached = reposDao.getAll()
            if (cached.isNotEmpty()) {
                return Result.success(cached.map { it.toDomainEntity() })
            }
        }
        return runCatching {
            val response = httpClient.get("user/repos") {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
            val repos: List<RepoEntity> = response.body()
            reposDao.upsertAll(repos.map { it.toDbEntity() })
            repos
        }.onFailure { if (it is CancellationException) throw it }
    }

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
}

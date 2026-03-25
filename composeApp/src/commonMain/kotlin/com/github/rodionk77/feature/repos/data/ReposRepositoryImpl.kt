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

class ReposRepositoryImpl(
    private val httpClient: HttpClient,
    private val reposDao: ReposDao,
    private val userDao: UserDao
) : ReposRepository {

    override suspend fun getRepositories(page: Int, perPage: Int, useCache: Boolean): Result<List<RepoEntity>> {
        return try {
            val response = httpClient.get("user/repos") {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
            val repos: List<RepoEntity> = response.body()
            reposDao.upsertAll(repos.map { it.toDbEntity() })
            Result.success(repos)
        } catch (e: Exception) {
            if (useCache && page == 1) {
                val cached = reposDao.getAll()
                if (cached.isNotEmpty()) {
                    Result.success(cached.map { it.toDomainEntity() })
                } else {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getProfile(): Result<UserEntity> {
        return try {
            val response = httpClient.get("user")
            val user: UserEntity = response.body()
            userDao.upsertUser(user.toDbEntity())
            Result.success(user)
        } catch (e: Exception) {
            val cached = userDao.getUser()
            if (cached != null) {
                Result.success(cached.toDomainEntity())
            } else {
                Result.failure(e)
            }
        }
    }
}

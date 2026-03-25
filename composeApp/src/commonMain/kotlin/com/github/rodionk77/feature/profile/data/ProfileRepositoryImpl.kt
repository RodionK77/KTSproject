package com.github.rodionk77.feature.profile.data

import com.github.rodionk77.common.TokenStorage
import com.github.rodionk77.common.database.AppDatabase
import com.github.rodionk77.common.models.UserEntity
import com.github.rodionk77.feature.profile.domain.ProfileRepository
import com.github.rodionk77.feature.repos.data.room.UserDao
import com.github.rodionk77.feature.repos.data.room.toDbEntity
import com.github.rodionk77.feature.repos.data.room.toDomainEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProfileRepositoryImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
    private val userDao: UserDao,
    private val database: AppDatabase
) : ProfileRepository {

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

    override suspend fun getCachedProfile(): UserEntity? {
        return userDao.getUser()?.toDomainEntity()
    }

    override suspend fun logout() {
        tokenStorage.clearAll()
        database.clearAllData()
    }
}

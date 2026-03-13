package com.github.rodionk77.feature.repoDescription.data

import com.github.rodionk77.feature.repoDescription.data.room.RepoDescriptionDao
import com.github.rodionk77.feature.repoDescription.data.room.toDbEntity
import com.github.rodionk77.feature.repoDescription.data.room.toDomainEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class RepoDescriptionRepository (
    private val httpClient: HttpClient,
    private val repoDescriptionDao: RepoDescriptionDao
){

    suspend fun getRepository(ownerLogin: String, repoName: String): Result<RepoDescriptionEntity> {
        return try {
            val response = httpClient.get("repos/$ownerLogin/$repoName")
            val repo: RepoDescriptionEntity = response.body()
            repoDescriptionDao.upsert(repo.toDbEntity())
            Result.success(repo)
        } catch (e: Exception) {
            val cached = repoDescriptionDao.get(repoName, ownerLogin)
            if (cached != null) {
                Result.success(cached.toDomainEntity())
            } else {
                Result.failure(e)
            }
        }
    }
}
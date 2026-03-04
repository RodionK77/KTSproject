package com.github.rodionk77.feature.repoDescription.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class RepoDescriptionRepository (
    private val httpClient: HttpClient
){

    suspend fun getRepository(ownerLogin: String, repoName: String): Result<RepoDescriptionEntity> {
        return try {
            val response = httpClient.get("repos/$ownerLogin/$repoName")
            val repo: RepoDescriptionEntity = response.body()
            Result.success(repo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
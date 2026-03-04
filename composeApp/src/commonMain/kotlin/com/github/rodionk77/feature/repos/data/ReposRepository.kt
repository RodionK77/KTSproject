package com.github.rodionk77.feature.repos.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ReposRepository(
    private val httpClient: HttpClient,
)  {

    suspend fun getRepositories(page: Int, perPage: Int = 20): Result<List<RepoEntity>> {
        return try {
            val response = httpClient.get("user/repos") {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
            val repos: List<RepoEntity> = response.body()
            Result.success(repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<UserEntity> {
        return try {
            val response = httpClient.get("user")
            val user: UserEntity = response.body()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
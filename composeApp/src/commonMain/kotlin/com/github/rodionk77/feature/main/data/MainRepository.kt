package com.github.rodionk77.feature.main.data

import com.github.rodionk77.common.TokenNotFoundException
import com.github.rodionk77.common.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.token_not_detected
import org.jetbrains.compose.resources.getString

class MainRepository(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
)  {

    suspend fun getRepositories(): Result<List<RepoEntity>> {
        val token = tokenStorage.getToken()
            ?: return Result.failure(TokenNotFoundException())

        return try {
            val response = httpClient.get("https://api.github.com/user/repos") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, "application/vnd.github.v3+json")
            }

            val repos: List<RepoEntity> = response.body()
            Result.success(repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<UserEntity> {
        val token = tokenStorage.getToken()
            ?: return Result.failure(TokenNotFoundException())

        return try {
            val response = httpClient.get("https://api.github.com/user") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, "application/vnd.github.v3+json")
            }

            val user: UserEntity = response.body()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
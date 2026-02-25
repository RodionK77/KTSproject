package com.github.rodionk77.feature.login.data

import com.github.rodionk77.common.TokenStorage
import com.github.rodionk77.common.Tokens
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.unknown_server_answer
import org.jetbrains.compose.resources.getString

class AuthRepository (
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
)  {

    suspend fun exchangeCodeForToken(code: String): Result<String> {
        return try {
            val response = httpClient.post {
                url("https://github.com/login/oauth/access_token")
                header(HttpHeaders.Accept, "application/json")
                url {
                    parameters.append("client_id", Tokens.GITHUB_CLIENT_ID)
                    parameters.append("client_secret", Tokens.GITHUB_CLIENT_SECRET)
                    parameters.append("code", code)
                }
            }

            val tokenResponse: GitHubTokenResponse = response.body()

            if (tokenResponse.error != null) {
                Result.failure(Exception("GitHub Error: ${tokenResponse.errorDescription ?: tokenResponse.error}"))
            }
            else if (tokenResponse.accessToken != null) {
                Result.success(tokenResponse.accessToken)
            }
            else {
                Result.failure(Exception(getString(Res.string.unknown_server_answer)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveToken(token: String) {
        tokenStorage.saveToken(token)
        Napier.d { "Токен успешно получен и сохранен: $token" }
    }
}
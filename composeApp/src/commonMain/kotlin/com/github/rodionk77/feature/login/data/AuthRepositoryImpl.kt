package com.github.rodionk77.feature.login.data

import com.github.rodionk77.common.NetworkConstants
import com.github.rodionk77.common.Utils.GitHubApiException
import com.github.rodionk77.common.TokenStorage
import com.github.rodionk77.common.Tokens
import com.github.rodionk77.common.Utils.TokenNotFoundException
import com.github.rodionk77.common.Utils.UnknownServerException
import com.github.rodionk77.feature.login.domain.AuthRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun exchangeCodeForToken(code: String): Result<String> {
        return try {
            val response = httpClient.post {
                url(NetworkConstants.GITHUB_AUTH_URL)
                header(HttpHeaders.Accept, NetworkConstants.GITHUB_AUTH_ACCEPT_HEADER)
                url {
                    parameters.append("client_id", Tokens.GITHUB_CLIENT_ID)
                    parameters.append("client_secret", Tokens.GITHUB_CLIENT_SECRET)
                    parameters.append("code", code)
                }
            }

            val tokenResponse: GitHubTokenResponse = response.body()

            if (tokenResponse.error != null) {
                Result.failure(GitHubApiException("GitHub Error: ${tokenResponse.errorDescription ?: tokenResponse.error}"))
            }
            else if (tokenResponse.accessToken != null) {
                tokenStorage.saveToken(tokenResponse.accessToken)
                Napier.d { "Токен успешно получен и сохранен: ${tokenResponse.accessToken}" }
                tokenResponse.refreshToken?.let { tokenStorage.saveRefreshToken(it) }
                Napier.d { "Токен обновления успешно получен и сохранен: ${tokenResponse.refreshToken}" }
                Result.success(tokenResponse.accessToken)
            }
            else {
                Result.failure(UnknownServerException())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshAccessToken(): Result<String> {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: return Result.failure(TokenNotFoundException())

        return try {
            val response = httpClient.post {
                url(NetworkConstants.GITHUB_AUTH_URL)
                header(HttpHeaders.Accept, NetworkConstants.GITHUB_AUTH_ACCEPT_HEADER)
                url {
                    parameters.append("client_id", Tokens.GITHUB_CLIENT_ID)
                    parameters.append("client_secret", Tokens.GITHUB_CLIENT_SECRET)
                    parameters.append("grant_type", "refresh_token")
                    parameters.append("refresh_token", refreshToken)
                }
            }
            val tokenResponse: GitHubTokenResponse = response.body()

            if (tokenResponse.accessToken != null) {
                tokenStorage.saveToken(tokenResponse.accessToken)
                tokenResponse.refreshToken?.let { tokenStorage.saveRefreshToken(it) }
                Result.success(tokenResponse.accessToken)
            } else {
                tokenStorage.clearAll()
                Result.failure(TokenNotFoundException())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun saveToken(token: String) {
        tokenStorage.saveToken(token)
        Napier.d { "Токен успешно получен и сохранен: $token" }
    }

    override fun getToken(): String? {
        return tokenStorage.getToken()
    }
}

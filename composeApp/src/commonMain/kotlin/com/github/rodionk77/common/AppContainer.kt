package com.github.rodionk77.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.rodionk77.common.Utils.TokenNotFoundException
import com.github.rodionk77.feature.login.data.AuthRepository
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionRepository
import com.github.rodionk77.feature.repos.data.ReposRepository
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.getValue

object AppContainer {

    init {
        Napier.base(DebugAntilog())
    }

    lateinit var dataStore: DataStore<Preferences>

    val tokenStorage: TokenStorage by lazy {
        TokenStorage(dataStore)
    }

    val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(message, tag = "Ktor")
                }
            }
            level = LogLevel.ALL
        }
        defaultRequest {
            url("https://api.github.com/")
            header(HttpHeaders.Accept, "application/vnd.github.v3+json")
        }
    }.also { client ->
        client.plugin(HttpSend).intercept { request ->
            if (request.url.host == "api.github.com") {
                val token = tokenStorage.getToken()
                    ?: throw TokenNotFoundException()
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
            execute(request)
        }
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(httpClient, tokenStorage)
    }

    val reposRepository: ReposRepository by lazy {
        ReposRepository(httpClient)
    }

    val repoDescriptionRepository: RepoDescriptionRepository by lazy {
        RepoDescriptionRepository(httpClient)
    }
}
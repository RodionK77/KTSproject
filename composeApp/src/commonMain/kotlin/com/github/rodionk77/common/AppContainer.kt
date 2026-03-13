package com.github.rodionk77.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.github.rodionk77.common.Utils.TokenNotFoundException
import com.github.rodionk77.common.database.AppDatabase
import com.github.rodionk77.feature.login.data.AuthRepository
import com.github.rodionk77.feature.profile.data.ProfileRepository
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionRepository
import com.github.rodionk77.feature.repos.data.ReposRepository
import com.liftric.kvault.KVault
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
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.getValue

object AppContainer {

    init {
        Napier.base(DebugAntilog())
    }

    //lateinit var dataStore: DataStore<Preferences>


    lateinit var database: AppDatabase

    lateinit var kVault: KVault  // ← вместо dataStore

    val tokenStorage: TokenStorage by lazy {
        TokenStorage(kVault)
    }

    private val baseHttpClient: HttpClient = HttpClient {
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
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(baseHttpClient, tokenStorage)
    }

    val httpClient: HttpClient = baseHttpClient.also { client ->
        client.plugin(HttpSend).intercept { request ->

            if (request.url.host != "api.github.com") {
                return@intercept execute(request)
            }

            val token = tokenStorage.getToken() ?: throw TokenNotFoundException()
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")

            val call = execute(request)

            if (call.response.status == HttpStatusCode.Unauthorized) {
                val refreshResult = authRepository.refreshAccessToken()
                if (refreshResult.isSuccess) {
                    val newToken = refreshResult.getOrNull()!!
                    request.headers.remove(HttpHeaders.Authorization)
                    request.headers.append(HttpHeaders.Authorization, "Bearer $newToken")
                    execute(request)
                } else {
                    tokenStorage.clearAll()
                    throw TokenNotFoundException()
                }
            } else {
                call
            }
        }
    }

    val reposRepository: ReposRepository by lazy {
        ReposRepository(httpClient, database.reposDao(), database.userDao())
    }

    val repoDescriptionRepository: RepoDescriptionRepository by lazy {
        RepoDescriptionRepository(httpClient, database.repoDescriptionDao())
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(
            httpClient,
            tokenStorage,
            database.userDao(),
            database
        )
    }
}
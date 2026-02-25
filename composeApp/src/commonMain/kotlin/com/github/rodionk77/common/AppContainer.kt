package com.github.rodionk77.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.rodionk77.feature.login.data.AuthRepository
import com.github.rodionk77.feature.main.data.MainRepository
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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

    val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(httpClient, tokenStorage)
    }

    val mainRepository: MainRepository by lazy {
        MainRepository(httpClient, tokenStorage)
    }

}
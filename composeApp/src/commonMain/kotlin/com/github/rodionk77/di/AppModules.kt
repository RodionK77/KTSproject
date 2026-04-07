package com.github.rodionk77.di

import com.github.rodionk77.common.Constants
import com.github.rodionk77.common.TokenStorage
import com.github.rodionk77.common.Utils.TokenNotFoundException
import com.github.rodionk77.common.database.AppDatabase
import com.github.rodionk77.feature.favorites.domain.FavoritesRepository
import com.github.rodionk77.feature.favorites.data.FavoritesRepositoryImpl
import com.github.rodionk77.feature.favorites.presentation.FavoritesViewModel
import com.github.rodionk77.feature.login.domain.AuthRepository
import com.github.rodionk77.feature.login.data.AuthRepositoryImpl
import com.github.rodionk77.feature.login.presentation.LoginViewModel
import com.github.rodionk77.feature.login.presentation.WelcomeViewModel
import com.github.rodionk77.feature.profile.domain.ProfileRepository
import com.github.rodionk77.feature.profile.data.ProfileRepositoryImpl
import com.github.rodionk77.feature.profile.presentation.ProfileViewModel
import com.github.rodionk77.feature.repoDetails.domain.RepoDetailsRepository
import com.github.rodionk77.feature.repoDetails.data.RepoDetailsRepositoryImpl
import com.github.rodionk77.feature.repoDetails.presentation.RepoDetailsViewModel
import com.github.rodionk77.feature.repoDetails.presentation.RepoFilesViewModel
import com.github.rodionk77.feature.repos.domain.ReposRepository
import com.github.rodionk77.feature.repos.data.ReposRepositoryImpl
import com.github.rodionk77.feature.repos.presentation.ReposViewModel
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { TokenStorage(get()) }

    single<HttpClient> {
        val tokenStorage = get<TokenStorage>()
        val koin = getKoin()

        HttpClient {
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
                url(Constants.GITHUB_API_BASE_URL)
                header(HttpHeaders.Accept, Constants.GITHUB_ACCEPT_HEADER)
            }
        }.also { client ->
            val tokenRefreshMutex = Mutex()

            client.plugin(HttpSend).intercept { request ->
                if (request.url.host != Constants.GITHUB_API_HOST) {
                    return@intercept execute(request)
                }

                val token = tokenStorage.getToken() ?: throw TokenNotFoundException()
                request.headers.append(HttpHeaders.Authorization, "${Constants.BEARER_PREFIX} $token")

                val call = execute(request)

                if (call.response.status == HttpStatusCode.Unauthorized) {
                    tokenRefreshMutex.withLock {
                        val refreshedToken = tokenStorage.getToken()
                        if (refreshedToken != null && refreshedToken != token) {
                            request.headers.remove(HttpHeaders.Authorization)
                            request.headers.append(HttpHeaders.Authorization, "${Constants.BEARER_PREFIX} $refreshedToken")
                            execute(request)
                        } else {
                            val refreshResult = koin.get<AuthRepository>().refreshAccessToken()
                            refreshResult.fold(
                                onSuccess = { newToken ->
                                    request.headers.remove(HttpHeaders.Authorization)
                                    request.headers.append(HttpHeaders.Authorization, "${Constants.BEARER_PREFIX} $newToken")
                                    execute(request)
                                },
                                onFailure = {
                                    tokenStorage.clearAll()
                                    throw TokenNotFoundException()
                                }
                            )
                        }
                    }
                } else {
                    call
                }
            }
        }
    }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ReposRepository> { ReposRepositoryImpl(get(), get<AppDatabase>().reposDao(), get<AppDatabase>().userDao()) }
    single<RepoDetailsRepository> { RepoDetailsRepositoryImpl(get(), get<AppDatabase>().repoDescriptionDao()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get<AppDatabase>().favoriteDao()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get<AppDatabase>().userDao(), get()) }
}

val viewModelModule = module {
    viewModel { WelcomeViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ReposViewModel(get()) }
    viewModel { RepoDetailsViewModel(get(), get(), get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { RepoFilesViewModel(get(), get()) }
}

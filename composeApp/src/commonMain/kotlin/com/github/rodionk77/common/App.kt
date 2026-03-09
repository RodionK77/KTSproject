package com.github.rodionk77.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.github.rodionk77.common.AppContainer.authRepository
import com.github.rodionk77.common.AppContainer.repoDescriptionRepository
import com.github.rodionk77.common.AppContainer.reposRepository
import com.github.rodionk77.feature.login.presentation.LoginScreen
import com.github.rodionk77.feature.login.presentation.LoginViewModel
import com.github.rodionk77.feature.login.presentation.WelcomeScreen
import com.github.rodionk77.feature.repoDescription.presentation.RepoDescriptionScreen
import com.github.rodionk77.feature.repoDescription.presentation.RepoDescriptionViewModel
import com.github.rodionk77.feature.repos.presentation.ReposScreen
import com.github.rodionk77.feature.repos.presentation.ReposViewModel
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Welcome : Route()

    @Serializable
    data class Login(val code: String? = null) : Route()

    @Serializable
    data object Repos : Route()

    @Serializable
    data class RepoDescription(val repoName: String, val ownerLogin: String) : Route()
}

@Composable
@Preview
fun App() {

    MaterialTheme {

        val navController = rememberNavController()

        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Welcome,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<Route.Welcome> {
                    WelcomeScreen(
                        onNavigateToLogin = {
                            navController.navigate(Route.Login()) {
                                popUpTo(Route.Welcome) { inclusive = false }
                            }
                        }
                    )
                }
                composable<Route.Login>(
                    deepLinks = listOf(
                        navDeepLink<Route.Login>(basePath = "myapp://oauth2callback")
                    )
                ) {
                    LoginScreen(
                        viewModel = viewModel {
                            LoginViewModel(repository = authRepository, savedStateHandle = createSavedStateHandle())
                        },
                        onNavigateToMain = {
                            navController.navigate(Route.Repos) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable<Route.Repos> {
                    ReposScreen(
                        viewModel = viewModel {
                            ReposViewModel(repository = reposRepository)
                        },
                        onNavigateToRepo = { repoName, ownerLogin ->
                            navController.navigate(Route.RepoDescription(repoName, ownerLogin))
                        })
                }
                composable<Route.RepoDescription> {
                    RepoDescriptionScreen(
                        viewModel = viewModel {
                            RepoDescriptionViewModel(
                                repository = repoDescriptionRepository,
                                savedStateHandle = createSavedStateHandle()
                            )
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }

    }

}

object DeepLinkManager {
    val deepLinkEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    fun handleIosUrl(url: String) {
        deepLinkEvent.tryEmit(url)
    }
}
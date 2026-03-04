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
object WelcomeRoute
@Serializable
data class LoginRoute(val code: String? = null)
@Serializable
object ReposRoute
@Serializable
data class RepoDescriptionRoute(val repoName: String, val ownerLogin: String)


@Composable
@Preview
fun App() {

    MaterialTheme {

        val navController = rememberNavController()

        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = WelcomeRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<WelcomeRoute> {
                    WelcomeScreen(
                        onNavigateToLogin = {
                            navController.navigate(LoginRoute()) {
                                popUpTo(WelcomeRoute) { inclusive = false }
                            }
                        }
                    )
                }
                composable<LoginRoute>(
                    deepLinks = listOf(
                        navDeepLink<LoginRoute>(basePath = "myapp://oauth2callback")
                    )
                ) {
                    val loginViewModel = viewModel {
                        LoginViewModel(repository = authRepository, savedStateHandle = createSavedStateHandle())
                    }

                    LaunchedEffect(Unit) {
                        DeepLinkManager.deepLinkEvent.collect { urlString ->
                            if (urlString.startsWith("myapp://oauth2callback")) {
                                val code = Url(urlString).parameters["code"]
                                if (code != null) {
                                    loginViewModel.handleOAuthCode(code)
                                }
                            }
                        }
                    }

                    LoginScreen(
                        viewModel = loginViewModel,
                        onNavigateToMain = {
                            navController.navigate(ReposRoute) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable<ReposRoute> {
                    val reposViewModel = viewModel {
                        ReposViewModel(repository = reposRepository)
                    }
                    ReposScreen(
                        viewModel = reposViewModel,
                        onNavigateToRepo = { repoName, ownerLogin ->
                            navController.navigate(RepoDescriptionRoute(repoName, ownerLogin))
                        })
                }
                composable<RepoDescriptionRoute> {
                    val repoDescriptionViewModel = viewModel {
                        RepoDescriptionViewModel(
                            repository = repoDescriptionRepository,
                            savedStateHandle = createSavedStateHandle()
                        )
                    }
                    RepoDescriptionScreen(
                        viewModel = repoDescriptionViewModel,
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
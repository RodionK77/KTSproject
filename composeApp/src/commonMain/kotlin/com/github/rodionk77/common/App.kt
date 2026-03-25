package com.github.rodionk77.common

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.github.rodionk77.feature.favorites.presentation.FavoritesScreen
import com.github.rodionk77.feature.favorites.presentation.FavoritesViewModel
import com.github.rodionk77.feature.login.presentation.LoginScreen
import com.github.rodionk77.feature.login.presentation.LoginViewModel
import com.github.rodionk77.feature.login.presentation.WelcomeScreen
import com.github.rodionk77.feature.login.presentation.WelcomeViewModel
import com.github.rodionk77.feature.profile.presentation.ProfileScreen
import com.github.rodionk77.feature.profile.presentation.ProfileViewModel
import com.github.rodionk77.feature.repoDetails.presentation.RepoDetailsScreen
import com.github.rodionk77.feature.repoDetails.presentation.RepoDetailsViewModel
import com.github.rodionk77.feature.repoDetails.presentation.RepoFilesScreen
import com.github.rodionk77.feature.repoDetails.presentation.RepoFilesViewModel
import com.github.rodionk77.feature.repos.presentation.ReposScreen
import com.github.rodionk77.feature.repos.presentation.ReposViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.account_circle_icon
import ktsproject.composeapp.generated.resources.bookmark_icon
import ktsproject.composeapp.generated.resources.favorites
import ktsproject.composeapp.generated.resources.profile
import ktsproject.composeapp.generated.resources.repositories
import ktsproject.composeapp.generated.resources.stacks_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
@Preview
fun App() {

    MaterialTheme {

        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStack?.destination?.route

        val showBottomBar = currentRoute?.let {
            it.contains("Welcome") || it.contains("Login")
        } == false


        Scaffold(
            bottomBar = {BottomBar(showBottomBar, navController)},
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Welcome,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(animationSpec = tween(600)) },
                exitTransition = { fadeOut(animationSpec = tween(600)) },
                popEnterTransition = { fadeIn(animationSpec = tween(600)) },
                popExitTransition = { fadeOut(animationSpec = tween(600)) }
            ) {
                composable<Route.Welcome> {
                    WelcomeScreen(
                        viewModel = koinViewModel<WelcomeViewModel>(),
                        onNavigateToLogin = {
                            navController.navigate(Route.Login()) {
                                popUpTo(Route.Welcome) { inclusive = false }
                            }
                        },
                        onNavigateToRepos = {
                            navController.navigate(Route.Repos) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable<Route.Login>(
                    deepLinks = listOf(
                        navDeepLink<Route.Login>(basePath = NetworkConstants.DEEP_LINK_PATH)
                    )
                ) {
                    LoginScreen(
                        viewModel = koinViewModel<LoginViewModel>(),
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
                        viewModel = koinViewModel<ReposViewModel>(),
                        onNavigateToRepo = { repoName, ownerLogin ->
                            navController.navigate(Route.RepoDetails(repoName, ownerLogin))
                        },
                        onNavigateToLogin = {
                            navController.navigate(Route.Login()) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        )
                }
                composable<Route.RepoDetails> {
                    RepoDetailsScreen(
                        viewModel = koinViewModel<RepoDetailsViewModel>(),
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToFiles = { repoName, ownerLogin ->
                            navController.navigate(Route.RepoFiles(repoName, ownerLogin))
                        }
                    )
                }
                composable<Route.RepoFiles> {
                    RepoFilesScreen(
                        viewModel = koinViewModel<RepoFilesViewModel>(),
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToSubdir = { repoName, ownerLogin, path ->
                            navController.navigate(Route.RepoFiles(repoName, ownerLogin, path))
                        },
                        onNavigateToDescription = {
                            navController.popBackStack<Route.RepoDetails>(inclusive = false)
                        }
                    )
                }
                composable<Route.Favorites> {
                    FavoritesScreen(
                        viewModel = koinViewModel<FavoritesViewModel>(),
                        onNavigateToRepo = { repoName, ownerLogin ->
                            navController.navigate(Route.RepoDetails(repoName, ownerLogin))
                        }
                    )
                }
                composable<Route.Profile> {
                    ProfileScreen(
                        viewModel = koinViewModel<ProfileViewModel>(),
                        onNavigateToLogin = {
                            navController.navigate(Route.Login()) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
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

@Composable
fun BottomBar(showBottomBar: Boolean, navController: NavHostController) {
    if (showBottomBar) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val isReposSelected = currentRoute?.contains("Repos") == true &&
                !currentRoute.contains("RepoDescription")
        val isFavoritesSelected = currentRoute?.contains("Favorites") == true
        val isProfileSelected = currentRoute?.contains("Profile") == true

        NavigationBar(
            tonalElevation = 0.dp,
        ) {
            NavigationBarItem(
                selected = isReposSelected,
                onClick = {
                    if (!isReposSelected) {
                        navController.navigate(Route.Repos) {
                            popUpTo(Route.Repos) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painterResource(Res.drawable.stacks_icon),
                        contentDescription = stringResource(Res.string.repositories)
                    )
                },
                label = { Text(stringResource(Res.string.repositories)) }
            )
            NavigationBarItem(
                selected = isFavoritesSelected,
                onClick = {
                    if (!isFavoritesSelected) {
                        navController.navigate(Route.Favorites) {
                            popUpTo(Route.Repos) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painterResource(Res.drawable.bookmark_icon),
                        contentDescription = stringResource(Res.string.favorites)
                    )
                },
                label = { Text(stringResource(Res.string.favorites)) }
            )
            NavigationBarItem(
                selected = isProfileSelected,
                onClick = {
                    if (!isProfileSelected) {
                        navController.navigate(Route.Profile) {
                            popUpTo(Route.Repos) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painterResource(Res.drawable.account_circle_icon),
                        contentDescription = stringResource(Res.string.profile)
                    )
                },
                label = { Text(stringResource(Res.string.profile)) }
            )
        }
    }
}

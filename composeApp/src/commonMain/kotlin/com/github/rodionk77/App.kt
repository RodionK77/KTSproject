package com.github.rodionk77

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.rodionk77.screens.LoginScreen
import com.github.rodionk77.screens.WelcomeScreen
import kotlinx.serialization.Serializable

@Serializable
object WelcomeRoute
@Serializable
object LoginRoute


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
                            navController.navigate(LoginRoute) {
                                popUpTo(WelcomeRoute) { inclusive = false }
                            }
                        }
                    )
                }
                composable<LoginRoute> { LoginScreen() }
            }
        }

    }

}
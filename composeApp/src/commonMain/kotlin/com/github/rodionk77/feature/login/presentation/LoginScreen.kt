package com.github.rodionk77.feature.login.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.github.rodionk77.common.Tokens
import io.github.aakira.napier.Napier
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.broken_image_icon
import ktsproject.composeapp.generated.resources.enter_github
import ktsproject.composeapp.generated.resources.hourglass_icon
import ktsproject.composeapp.generated.resources.login_image
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMain: () -> Unit
) {

    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { event ->
            when (event) {
                is LoginUiEvent.NavigateToMain -> onNavigateToMain()
            }
        }
    }

    val launchGitHubAuth = {
        val redirectUri = "myapp://oauth2callback"
        val authUrl = "https://github.com/login/oauth/authorize?client_id=${Tokens.GITHUB_CLIENT_ID}&redirect_uri=$redirectUri&scope=repo,user"

        uriHandler.openUri(authUrl)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data("https://learn.microsoft.com/ru-ru/training/media/products/github/github-overview.png")
                .httpHeaders(
                    NetworkHeaders.Builder()
                        .set("User-Agent", "Android Studio")
                        .build()
                )
                .build(),
            error = painterResource(Res.drawable.broken_image_icon),
            placeholder = painterResource(Res.drawable.hourglass_icon),
            contentDescription = stringResource(Res.string.login_image),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(32.dp))

        uiState.error?.let { errorText ->
            Text(
                text = errorText.asString(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedButton(
            onClick = launchGitHubAuth,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = stringResource(Res.string.enter_github),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

    }
}

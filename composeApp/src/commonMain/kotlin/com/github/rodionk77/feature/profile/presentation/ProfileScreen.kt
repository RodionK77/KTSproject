package com.github.rodionk77.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.business_center_24px
import ktsproject.composeapp.generated.resources.calendar_icon
import ktsproject.composeapp.generated.resources.error
import ktsproject.composeapp.generated.resources.followers
import ktsproject.composeapp.generated.resources.following
import ktsproject.composeapp.generated.resources.link_icon
import ktsproject.composeapp.generated.resources.location_icon
import ktsproject.composeapp.generated.resources.logout
import ktsproject.composeapp.generated.resources.mail_icon
import ktsproject.composeapp.generated.resources.member_since
import ktsproject.composeapp.generated.resources.repositories
import ktsproject.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { event ->
            when (event) {
                is ProfileUiEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${stringResource(Res.string.error)}: ${uiState.error!!.asString()}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { viewModel.retry() }) {
                    Text(stringResource(Res.string.retry))
                }
            }
        }

        else -> {
            val user = uiState.user
            Napier.d { user.toString() }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = user.login,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                if (!user.name.isNullOrBlank()) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!user.bio.isNullOrBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.repositories),
                        value = user.publicRepos.toString()
                    )
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.followers),
                        value = user.followers.toString()
                    )
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.following),
                        value = user.following.toString()
                    )
                }

                HorizontalDivider()

                if (!user.company.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.business_center_24px,
                        text = user.company
                    )
                }
                if (!user.location.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.location_icon,
                        text = user.location
                    )
                }
                if (!user.email.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.mail_icon,
                        text = user.email
                    )
                }
                if (!user.blog.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.link_icon,
                        text = user.blog
                    )
                }
                if (!user.createdAt.isNullOrBlank()) {
                    val formattedDate = user.createdAt
                        .substringBefore("T")
                        .split("-")
                        .reversed()
                        .joinToString(".")

                    ProfileInfoRow(
                        icon = Res.drawable.calendar_icon,
                        text = "${stringResource(Res.string.member_since)}: $formattedDate"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(Res.string.logout))
                }
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileInfoRow(icon: DrawableResource, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
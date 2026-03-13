package com.github.rodionk77.feature.repoDescription.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.arrow_back_icon
import ktsproject.composeapp.generated.resources.back_icon
import ktsproject.composeapp.generated.resources.created_at
import ktsproject.composeapp.generated.resources.default_branch
import ktsproject.composeapp.generated.resources.error
import ktsproject.composeapp.generated.resources.forks
import ktsproject.composeapp.generated.resources.language
import ktsproject.composeapp.generated.resources.open_issues
import ktsproject.composeapp.generated.resources.open_on_github
import ktsproject.composeapp.generated.resources.private_repo
import ktsproject.composeapp.generated.resources.public_repo
import ktsproject.composeapp.generated.resources.retry
import ktsproject.composeapp.generated.resources.stars
import ktsproject.composeapp.generated.resources.updated_at
import ktsproject.composeapp.generated.resources.visibility
import ktsproject.composeapp.generated.resources.watchers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RepoDescriptionScreen(
    viewModel: RepoDescriptionViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.arrow_back_icon),
                contentDescription = stringResource(Res.string.back_icon),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
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

            uiState.repo != null -> {
                val repo = uiState.repo!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = repo.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (!repo.description.isNullOrBlank()) {
                        Text(
                            text = repo.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider()

                    if (!repo.language.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.language), repo.language)
                    }

                    RepoInfoRow(stringResource(Res.string.stars), repo.stargazersCount.toString())
                    RepoInfoRow(stringResource(Res.string.forks), repo.forksCount.toString())
                    RepoInfoRow(stringResource(Res.string.watchers), repo.watchersCount.toString())
                    RepoInfoRow(stringResource(Res.string.open_issues), repo.openIssuesCount.toString())

                    if (!repo.defaultBranch.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.default_branch), repo.defaultBranch)
                    }

                    if (!repo.createdAt.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.created_at), repo.createdAt)
                    }

                    if (!repo.updatedAt.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.updated_at), repo.updatedAt)
                    }

                    RepoInfoRow(
                        label = stringResource(Res.string.visibility),
                        value = if (repo.private) stringResource(Res.string.private_repo)
                        else stringResource(Res.string.public_repo)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { /*  */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.open_on_github))
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
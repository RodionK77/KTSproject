package com.github.rodionk77.feature.repos.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.error
import ktsproject.composeapp.generated.resources.no_repositories
import ktsproject.composeapp.generated.resources.search
import ktsproject.composeapp.generated.resources.search_icon
import ktsproject.composeapp.generated.resources.users_repositories
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.rodionk77.common.ui.RepoItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import ktsproject.composeapp.generated.resources.clear_icon
import ktsproject.composeapp.generated.resources.refresh_success
import ktsproject.composeapp.generated.resources.retry

@Composable
fun ReposScreen(
    viewModel: ReposViewModel,
    onNavigateToRepo: (repoName: String, ownerLogin: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(Res.string.refresh_success)

    val lifecycleOwner = LocalLifecycleOwner.current

    /*DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.cancelLoading()
        }
    }*/

    LaunchedEffect(Unit) {
        viewModel.effect.collect { event ->
            when (event) {
                is MainUiEvent.NavigateToLogin -> onNavigateToLogin()
                is MainUiEvent.RefreshSuccess -> {
                    snackbarHostState.showSnackbar(successMessage)
                }
                is MainUiEvent.RefreshError -> {
                    snackbarHostState.showSnackbar(event.message.asSuspendString())
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${stringResource(Res.string.users_repositories)} ${uiState.user.login}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text(stringResource(Res.string.search)) },
                    leadingIcon = {
                        Icon(
                            painterResource(Res.drawable.search_icon),
                            contentDescription = stringResource(Res.string.search_icon),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    painterResource(Res.drawable.clear_icon),
                                    contentDescription = stringResource(Res.string.clear_icon),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true
                )

                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }

                    uiState.error != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "${stringResource(Res.string.error)}: ${uiState.error!!.asString()}",
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.retry() }) {
                                Text(stringResource(Res.string.retry))
                            }
                        }
                    }

                    uiState.filteredRepos.isEmpty() -> {
                        Text(
                            text = stringResource(Res.string.no_repositories),
                        )
                    }

                    else -> ReposList(viewModel, uiState, onNavigateToRepo)
                }
            }
        }
    }
}

@Composable
fun ReposList(viewModel: ReposViewModel, uiState: MainUiState, onNavigateToRepo: (repoName: String, ownerLogin: String) -> Unit){
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val total = listState.layoutInfo.totalItemsCount
            lastVisible != null && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadNextPage()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = uiState.filteredRepos,
            key = { repo -> repo.id }
        ) { repo ->
            RepoItem(
                repo = repo,
                avatar = repo.owner.avatarUrl ?: "",
                onClick = { onNavigateToRepo(repo.name, repo.owner.login) }
            )
        }

        if (uiState.isPaginating) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}



package com.github.rodionk77.feature.repoDetails.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.rodionk77.common.Route
import com.github.rodionk77.common.Utils.UiText
import com.github.rodionk77.common.Utils.toUiText
import com.github.rodionk77.feature.favorites.domain.FavoritesRepository
import com.github.rodionk77.feature.repoDetails.data.RepoDetailsEntity
import com.github.rodionk77.feature.repoDetails.domain.RepoDetailsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ktsproject.composeapp.generated.resources.Res

sealed class IssueCreationStatus {
    data object Idle : IssueCreationStatus()
    data object Sending : IssueCreationStatus()
    data object Success : IssueCreationStatus()
    data class Error(val message: UiText) : IssueCreationStatus()
}

data class RepoDetailsUiState(
    val isLoading: Boolean = true,
    val repo: RepoDetailsEntity? = null,
    val error: UiText? = null,
    val isFavorite: Boolean = false,
    val readme: String? = null,
    val showCreateIssueDialog: Boolean = false,
    val issueCreationStatus: IssueCreationStatus = IssueCreationStatus.Idle
)

class RepoDetailsViewModel(
    private val repository: RepoDetailsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepoDetailsUiState())
    val uiState: StateFlow<RepoDetailsUiState> = _uiState.asStateFlow()

    private val repoName: String = savedStateHandle.toRoute<Route.RepoDetails>().repoName
    private val ownerLogin: String = savedStateHandle.toRoute<Route.RepoDetails>().ownerLogin

    private var loadJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            coroutineScope {
                val repoDeferred = async { repository.getRepository(ownerLogin, repoName) }
                val readmeDeferred = async { repository.getReadme(ownerLogin, repoName) }

                val result = repoDeferred.await()
                val readme = readmeDeferred.await().getOrNull()

                if (result.isSuccess && readme != null) {
                    repository.saveReadme(ownerLogin, repoName, readme)
                }

                result.fold(
                    onSuccess = { repo ->
                        val isFavorite = favoritesRepository.isFavorite(repo.id)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                repo = repo,
                                isFavorite = isFavorite,
                                readme = readme
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(isLoading = false, error = exception.toUiText()) }
                    }
                )
            }
        }
    }

    fun retry() {
        if (_uiState.value.isLoading) return
        loadData()
    }

    fun toggleFavorite() {
        val repo = _uiState.value.repo ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoritesRepository.removeFavorite(repo.id)
            } else {
                favoritesRepository.addFavorite(repo)
            }
            _uiState.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun showCreateIssueDialog() {
        _uiState.update { it.copy(showCreateIssueDialog = true, issueCreationStatus = IssueCreationStatus.Idle) }
    }

    fun dismissCreateIssueDialog() {
        _uiState.update { it.copy(showCreateIssueDialog = false, issueCreationStatus = IssueCreationStatus.Idle) }
    }

    fun createIssue(title: String, body: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(issueCreationStatus = IssueCreationStatus.Sending) }
            val result = repository.createIssue(ownerLogin, repoName, title, body)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(issueCreationStatus = IssueCreationStatus.Success) }
                },
                onFailure = { exception ->
                    _uiState.update { it.copy(issueCreationStatus = IssueCreationStatus.Error(exception.toUiText())) }
                }
            )
        }
    }
}
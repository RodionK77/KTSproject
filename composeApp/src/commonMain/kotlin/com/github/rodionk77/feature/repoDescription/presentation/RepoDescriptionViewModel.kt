package com.github.rodionk77.feature.repoDescription.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.rodionk77.common.Route
import com.github.rodionk77.common.Utils.TokenNotFoundException
import com.github.rodionk77.common.Utils.UiText
import com.github.rodionk77.feature.favorites.data.FavoritesRepository
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionEntity
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.token_not_detected
import ktsproject.composeapp.generated.resources.unknown_error

data class RepoDescriptionUiState(
    val isLoading: Boolean = true,
    val repo: RepoDescriptionEntity? = null,
    val error: UiText? = null,
    val isFavorited: Boolean = false,
    val readme: String? = null
)

class RepoDescriptionViewModel(
    private val repository: RepoDescriptionRepository,
    private val favoritesRepository: FavoritesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepoDescriptionUiState())
    val uiState: StateFlow<RepoDescriptionUiState> = _uiState.asStateFlow()

    private val repoName: String = savedStateHandle.toRoute<Route.RepoDescription>().repoName
    private val ownerLogin: String = savedStateHandle.toRoute<Route.RepoDescription>().ownerLogin

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

                if (result.isSuccess) {
                    val repo = result.getOrNull()
                    val isFavorited = repo?.let { favoritesRepository.isFavorite(it.id) } ?: false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            repo = repo,
                            isFavorited = isFavorited,
                            readme = readme
                        )
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val errorText = when (exception) {
                        is TokenNotFoundException -> UiText.StringRes(Res.string.token_not_detected)
                        else -> exception?.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringRes(Res.string.unknown_error)
                    }
                    _uiState.update { it.copy(isLoading = false, error = errorText) }
                }
            }
        }
    }

    fun retry() {
        loadData()
    }

    fun toggleFavorite() {
        val repo = _uiState.value.repo ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorited) {
                favoritesRepository.removeFavorite(repo.id)
            } else {
                favoritesRepository.addFavorite(repo)
            }
            _uiState.update { it.copy(isFavorited = !it.isFavorited) }
        }
    }
}
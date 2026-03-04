package com.github.rodionk77.feature.repos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rodionk77.common.TokenNotFoundException
import com.github.rodionk77.common.UiText
import com.github.rodionk77.feature.repos.data.ReposRepository
import com.github.rodionk77.feature.repos.data.RepoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.token_not_detected
import ktsproject.composeapp.generated.resources.unknown_error

data class MainUiState(
    val isLoading: Boolean = true,
    val repos: List<RepoEntity> = emptyList(),
    val filteredRepos: List<RepoEntity> = emptyList(),
    val username: String? = null,
    val avatar: String? = null,
    val error: UiText? = null,
    val isPaginating: Boolean = false, //состояние подгрузки
    val hasReachedEnd: Boolean = false, //останавливает запросы, когда сервер вернул пустой список
    val currentPage: Int = 1
)

class ReposViewModel(
    private val repository: ReposRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadData()
        observeSearch()
    }

    private fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val profileDeferred = async { repository.getProfile() }
            val reposDeferred = async { repository.getRepositories(page = 1) }

            val profileResult = profileDeferred.await()
            val reposResult = reposDeferred.await()

            if (profileResult.isSuccess && reposResult.isSuccess) {
                val repos = reposResult.getOrNull() ?: emptyList()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        username = profileResult.getOrNull()?.login,
                        avatar = profileResult.getOrNull()?.avatarUrl,
                        repos = repos,
                        filteredRepos = repos,
                        currentPage = 1,
                        hasReachedEnd = repos.isEmpty()
                    )
                }
            } else {
                val exception = profileResult.exceptionOrNull() ?: reposResult.exceptionOrNull()

                val errorText = when (exception) {
                    is TokenNotFoundException -> UiText.StringRes(Res.string.token_not_detected)
                    else -> exception?.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringRes(Res.string.unknown_error)
                }

                _uiState.update { it.copy(isLoading = false, error = errorText) }
            }
        }
    }

    fun retry() {
        loadData()
    }

    fun cancelLoading() {
        loadJob?.cancel()
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isPaginating || state.hasReachedEnd || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPaginating = true) }

            val nextPage = state.currentPage + 1
            val result = repository.getRepositories(page = nextPage)

            if (result.isSuccess) {
                val newRepos = result.getOrNull() ?: emptyList()
                val updatedRepos = state.repos + newRepos
                _uiState.update {
                    it.copy(
                        isPaginating = false,
                        repos = updatedRepos,
                        filteredRepos = updatedRepos,
                        currentPage = nextPage,
                        hasReachedEnd = newRepos.isEmpty()
                    )
                }
            } else {
                _uiState.update { it.copy(isPaginating = false) }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    flow {
                        val filtered = if (query.isBlank()) {
                            _uiState.value.repos
                        } else {
                            _uiState.value.repos.filter {
                                it.name.contains(query, ignoreCase = true)
                            }
                        }
                        emit(filtered)
                    }
                }
                .collect { filtered ->
                    _uiState.update { it.copy(filteredRepos = filtered) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
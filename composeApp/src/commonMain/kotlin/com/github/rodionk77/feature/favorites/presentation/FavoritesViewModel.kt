package com.github.rodionk77.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rodionk77.feature.favorites.data.FavoritesRepository
import com.github.rodionk77.common.models.RepoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val repos: List<RepoEntity> = emptyList(),
    val filteredRepos: List<RepoEntity> = emptyList()
)

class FavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadFavorites()
        observeSearch()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val repos = repository.getFavorites()
            _uiState.update {
                it.copy(isLoading = false, repos = repos, filteredRepos = repos)
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

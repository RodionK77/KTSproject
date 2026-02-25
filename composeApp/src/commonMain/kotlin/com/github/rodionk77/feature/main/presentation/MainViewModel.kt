package com.github.rodionk77.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rodionk77.feature.main.data.MainRepository
import com.github.rodionk77.feature.main.data.RepoEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = true,
    val repos: List<RepoEntity> = emptyList(),
    val username: String? = null,
    val avatar: String? = null,
    val error: String? = null
)

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val profileDeferred = async { repository.getProfile() }
            val reposDeferred = async { repository.getRepositories() }

            val profileResult = profileDeferred.await()
            val reposResult = reposDeferred.await()

            if (profileResult.isSuccess && reposResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        username = profileResult.getOrNull()?.login,
                        avatar = profileResult.getOrNull()?.avatarUrl,
                        repos = reposResult.getOrNull() ?: emptyList()
                    )
                }
            } else {
                val errorMsg = profileResult.exceptionOrNull()?.message
                    ?: reposResult.exceptionOrNull()?.message

                _uiState.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }
}
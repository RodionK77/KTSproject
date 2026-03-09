package com.github.rodionk77.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rodionk77.common.TokenNotFoundException
import com.github.rodionk77.common.UiText
import com.github.rodionk77.feature.main.data.MainRepository
import com.github.rodionk77.feature.main.data.RepoEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.token_not_detected
import ktsproject.composeapp.generated.resources.unknown_error

data class MainUiState(
    val isLoading: Boolean = true,
    val repos: List<RepoEntity> = emptyList(),
    val username: String? = null,
    val avatar: String? = null,
    val error: UiText? = null
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
}
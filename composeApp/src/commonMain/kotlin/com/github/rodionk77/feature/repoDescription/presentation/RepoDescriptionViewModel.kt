package com.github.rodionk77.feature.repoDescription.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.rodionk77.common.RepoDescriptionRoute
import com.github.rodionk77.common.TokenNotFoundException
import com.github.rodionk77.common.UiText
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionEntity
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionRepository
import kotlinx.coroutines.Job
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
    val error: UiText? = null
)

class RepoDescriptionViewModel(
    private val repository: RepoDescriptionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepoDescriptionUiState())
    val uiState: StateFlow<RepoDescriptionUiState> = _uiState.asStateFlow()

    private val repoName: String = savedStateHandle.toRoute<RepoDescriptionRoute>().repoName
    private val ownerLogin: String = savedStateHandle.toRoute<RepoDescriptionRoute>().ownerLogin

    private var loadJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getRepository(ownerLogin, repoName)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        repo = result.getOrNull()
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

    fun retry() {
        loadData()
    }
}
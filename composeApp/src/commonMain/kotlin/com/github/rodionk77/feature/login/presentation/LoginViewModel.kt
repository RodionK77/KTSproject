package com.github.rodionk77.feature.login.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.rodionk77.common.Route
import com.github.rodionk77.common.Utils.UiText
import com.github.rodionk77.common.Utils.toUiText
import com.github.rodionk77.feature.login.domain.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ktsproject.composeapp.generated.resources.Res

data class LoginUiState (
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed interface LoginUiEvent {
    object NavigateToRepos : LoginUiEvent
    /*data class ShowError(val message: UiText) : LoginUiEvent*/
}

class LoginViewModel(
    private val repository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<LoginUiEvent>()
    val effect: SharedFlow<LoginUiEvent> = _effect.asSharedFlow()

    private var loadJob: Job? = null

    init {
        val route = savedStateHandle.toRoute<Route.Login>()
        route.code?.let {
            handleOAuthCode(it)
        }
    }

    fun handleOAuthCode(code: String) {
        if (_uiState.value.isLoading) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update{it.copy(isLoading = true)}

            val result = repository.exchangeCodeForToken(code)

            result.onSuccess { token ->
                _uiState.update{it.copy(isLoading = false)}
                _effect.emit(LoginUiEvent.NavigateToRepos)
            }
            result.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.toUiText()) }
            }
        }
    }

    fun cancelLoading() {
        loadJob?.cancel()
    }
}
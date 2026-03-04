package com.github.rodionk77.feature.login.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.rodionk77.common.LoginRoute
import com.github.rodionk77.common.UiText
import com.github.rodionk77.common.UnknownServerException
import com.github.rodionk77.feature.login.data.AuthRepository
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
import ktsproject.composeapp.generated.resources.unknown_error
import ktsproject.composeapp.generated.resources.unknown_server_answer

data class LoginUiState (
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed interface LoginUiEvent {
    object NavigateToMain : LoginUiEvent
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
        val route = savedStateHandle.toRoute<LoginRoute>()
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
                repository.saveToken(token)
                _uiState.update{it.copy(isLoading = false)}
                _effect.emit(LoginUiEvent.NavigateToMain)
            }
            result.onFailure { exception ->
                val errorText = when (exception) {
                    is UnknownServerException -> UiText.StringRes(Res.string.unknown_server_answer)
                    else -> exception.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringRes(Res.string.unknown_error)
                }
                _uiState.update{it.copy(isLoading = false, error = errorText)}
            }
        }
    }

    fun cancelLoading() {
        loadJob?.cancel()
    }
}
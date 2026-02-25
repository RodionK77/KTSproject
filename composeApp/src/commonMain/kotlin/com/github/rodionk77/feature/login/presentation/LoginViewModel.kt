package com.github.rodionk77.feature.login.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.rodionk77.common.LoginRoute
import com.github.rodionk77.feature.login.data.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString


data class LoginUiState (
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginUiEvent {
    object NavigateToMain : LoginUiEvent
    data class ShowError(val message: String) : LoginUiEvent
}

class LoginViewModel(
    private val repository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<LoginUiEvent>()
    val effect: SharedFlow<LoginUiEvent> = _effect.asSharedFlow()

    init {
        val route = savedStateHandle.toRoute<LoginRoute>()
        route.code?.let {
            handleOAuthCode(it)
        }
    }

    fun handleOAuthCode(code: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.exchangeCodeForToken(code)

            result.onSuccess { token ->
                repository.saveToken(token)
                _uiState.value = _uiState.value.copy(isLoading = false)
                _effect.emit(LoginUiEvent.NavigateToMain)
            }
            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = exception.message)
                _effect.emit(LoginUiEvent.ShowError(exception.message ?: getString(Res.string.unknown_error)))
            }
        }
    }
}
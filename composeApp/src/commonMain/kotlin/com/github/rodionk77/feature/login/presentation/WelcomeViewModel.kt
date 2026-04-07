package com.github.rodionk77.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rodionk77.feature.login.domain.AuthRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WelcomeUiState {
    object Loading : WelcomeUiState
    object NoToken : WelcomeUiState
}

sealed interface WelcomeUiEvent {
    object NavigateToRepos : WelcomeUiEvent
    object NavigateToLogin : WelcomeUiEvent
}

class WelcomeViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WelcomeUiState>(WelcomeUiState.Loading)
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<WelcomeUiEvent>(replay = 1)
    val effect: SharedFlow<WelcomeUiEvent> = _effect.asSharedFlow()

    init {
        checkToken()
    }

    private fun checkToken() {
        viewModelScope.launch {
            val token = repository.getToken()
            when {
                token != null -> {
                    Napier.d { "Токен уже получен: $token" }
                    _effect.emit(WelcomeUiEvent.NavigateToRepos)
                }
                repository.hasSeenWelcome() -> _effect.emit(WelcomeUiEvent.NavigateToLogin)
                else -> _uiState.update { WelcomeUiState.NoToken }
            }
        }
    }

    fun onGoToLoginClicked() {
        repository.markWelcomeSeen()
        viewModelScope.launch {
            _effect.emit(WelcomeUiEvent.NavigateToLogin)
        }
    }
}
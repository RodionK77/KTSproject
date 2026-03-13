package com.github.rodionk77.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rodionk77.common.Utils.UiText
import com.github.rodionk77.feature.profile.data.ProfileRepository
import com.github.rodionk77.common.models.UserEntity
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

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserEntity = UserEntity(),
    val error: UiText? = null
)

sealed interface ProfileUiEvent {
    object NavigateToLogin : ProfileUiEvent
}

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileUiEvent>()
    val effect: SharedFlow<ProfileUiEvent> = _effect.asSharedFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val cached = repository.getCachedProfile()
            if (cached != null) {
                _uiState.update { it.copy(isLoading = false, user = cached) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = UiText.StringRes(Res.string.unknown_error)
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
           repository.logout()
            _effect.emit(ProfileUiEvent.NavigateToLogin)
        }
    }

    fun retry() = loadProfile()
}
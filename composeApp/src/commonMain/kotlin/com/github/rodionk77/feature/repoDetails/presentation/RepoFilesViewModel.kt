package com.github.rodionk77.feature.repoDetails.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.rodionk77.common.PickedFile
import com.github.rodionk77.common.Route
import com.github.rodionk77.common.Utils.HttpException
import com.github.rodionk77.common.Utils.TokenNotFoundException
import com.github.rodionk77.common.Utils.UiText
import com.github.rodionk77.feature.repoDetails.data.GitHubContentItem
import com.github.rodionk77.feature.repoDetails.domain.RepoDetailsRepository
import kotlinx.coroutines.Job
import org.jetbrains.compose.resources.getString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.error_403
import ktsproject.composeapp.generated.resources.error_404
import ktsproject.composeapp.generated.resources.no_internet
import ktsproject.composeapp.generated.resources.token_not_detected
import ktsproject.composeapp.generated.resources.unknown_error
import ktsproject.composeapp.generated.resources.upload_commit_message
import ktsproject.composeapp.generated.resources.error_409
import ktsproject.composeapp.generated.resources.error_422

sealed class UploadStatus {
    data object Idle : UploadStatus()
    data object Uploading : UploadStatus()
    data object Success : UploadStatus()
    data class Error(val message: UiText) : UploadStatus()
}

data class RepoFilesUiState(
    val isLoading: Boolean = true,
    val items: List<GitHubContentItem> = emptyList(),
    val error: UiText? = null,
    val currentPath: String = "",
    val selectedFile: GitHubContentItem? = null,
    val fileContent: String? = null,
    val isFileLoading: Boolean = false,
    val fileError: UiText? = null,
    val uploadStatus: UploadStatus = UploadStatus.Idle,
    val showUploadDialog: Boolean = false
)

class RepoFilesViewModel(
    private val repository: RepoDetailsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepoFilesUiState())
    val uiState: StateFlow<RepoFilesUiState> = _uiState.asStateFlow()

    private val route = savedStateHandle.toRoute<Route.RepoFiles>()
    val repoName: String = route.repoName
    val ownerLogin: String = route.ownerLogin
    private val path: String = route.path

    private var loadJob: Job? = null
    private var pendingUpload: PickedFile? = null
    val pendingFileName: String get() = pendingUpload?.name ?: ""

    init {
        _uiState.update { it.copy(currentPath = path) }
        loadContents()
    }

    private fun loadContents() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getContents(ownerLogin, repoName, path)

            result.fold(
                onSuccess = { items ->
                    _uiState.update { it.copy(isLoading = false, items = items) }
                },
                onFailure = { exception ->
                    val errorText = when {
                        exception is TokenNotFoundException ->
                            UiText.StringRes(Res.string.token_not_detected)
                        exception.message?.contains("UnknownHostException") == true ||
                                exception.message?.contains("Unable to resolve host") == true ||
                                exception.message?.contains("The Internet connection appears to be offline") == true ||
                                exception.message?.contains("Network is unreachable") == true ->
                            UiText.StringRes(Res.string.no_internet)
                        else -> exception.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringRes(Res.string.unknown_error)
                    }
                    _uiState.update { it.copy(isLoading = false, error = errorText) }
                }
            )
        }
    }

    fun retry() {
        loadContents()
    }

    fun openFile(item: GitHubContentItem) {
        val url = item.downloadUrl ?: return
        _uiState.update {
            it.copy(selectedFile = item, isFileLoading = true, fileContent = null, fileError = null)
        }
        viewModelScope.launch {
            val result = repository.getFileContent(url)
            result.fold(
                onSuccess = { content ->
                    _uiState.update { it.copy(isFileLoading = false, fileContent = content) }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            isFileLoading = false,
                            fileError = UiText.StringRes(Res.string.unknown_error)
                        )
                    }
                }
            )
        }
    }

    fun dismissFileSheet() {
        _uiState.update {
            it.copy(selectedFile = null, fileContent = null, fileError = null, isFileLoading = false)
        }
    }

    fun onFilePicked(picked: PickedFile) {
        pendingUpload = picked
        _uiState.update { it.copy(showUploadDialog = true, uploadStatus = UploadStatus.Idle) }
    }

    fun confirmUpload() {
        val picked = pendingUpload ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(uploadStatus = UploadStatus.Uploading) }
            val existingSha = _uiState.value.items
                .find { it.name == picked.name && it.isFile }
                ?.sha?.ifEmpty { null }
            val filePath = if (path.isEmpty()) picked.name else "$path/${picked.name}"
            val commitMessage = getString(Res.string.upload_commit_message, picked.name)
            repository.uploadFile(
                ownerLogin = ownerLogin,
                repoName = repoName,
                filePath = filePath,
                fileName = picked.name,
                fileBytes = picked.bytes,
                existingSha = existingSha,
                message = commitMessage
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(uploadStatus = UploadStatus.Success, showUploadDialog = false) }
                    pendingUpload = null
                    loadContents()
                },
                onFailure = { e ->
                    val errorText = when {
                        e is TokenNotFoundException ->
                            UiText.StringRes(Res.string.token_not_detected)
                        e.message?.contains("UnknownHostException") == true ||
                        e.message?.contains("Unable to resolve host") == true ||
                        e.message?.contains("The Internet connection appears to be offline") == true ||
                        e.message?.contains("Network is unreachable") == true ->
                            UiText.StringRes(Res.string.no_internet)
                        e is HttpException -> when (e.code) {
                            403 -> UiText.StringRes(Res.string.error_403)
                            404 -> UiText.StringRes(Res.string.error_404)
                            409 -> UiText.StringRes(Res.string.error_409)
                            422 -> UiText.StringRes(Res.string.error_422)
                            else -> UiText.StringRes(Res.string.unknown_error)
                        }
                        else -> e.message?.let { UiText.DynamicString(it) }
                            ?: UiText.StringRes(Res.string.unknown_error)
                    }
                    _uiState.update { it.copy(uploadStatus = UploadStatus.Error(errorText)) }
                }
            )
        }
    }

    fun dismissUploadDialog() {
        pendingUpload = null
        _uiState.update { it.copy(showUploadDialog = false, uploadStatus = UploadStatus.Idle) }
    }

    private fun mapNetworkError(exception: Throwable): UiText = when {
        exception is TokenNotFoundException ->
            UiText.StringRes(Res.string.token_not_detected)
        exception.message?.contains("UnknownHostException") == true ||
        exception.message?.contains("Unable to resolve host") == true ||
        exception.message?.contains("The Internet connection appears to be offline") == true ||
        exception.message?.contains("Network is unreachable") == true ->
            UiText.StringRes(Res.string.no_internet)
        else -> exception.message?.let { UiText.DynamicString(it) }
            ?: UiText.StringRes(Res.string.unknown_error)
    }
}

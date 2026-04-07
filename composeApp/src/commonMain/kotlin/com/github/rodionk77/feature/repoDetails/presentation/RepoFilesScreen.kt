package com.github.rodionk77.feature.repoDetails.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.rodionk77.common.rememberFilePicker
import com.github.rodionk77.feature.repoDetails.data.GitHubContentItem
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.arrow_back_icon
import ktsproject.composeapp.generated.resources.back_icon
import ktsproject.composeapp.generated.resources.cancel
import ktsproject.composeapp.generated.resources.cannot_preview
import ktsproject.composeapp.generated.resources.empty_directory
import ktsproject.composeapp.generated.resources.error
import ktsproject.composeapp.generated.resources.file_icon
import ktsproject.composeapp.generated.resources.file_icon_desc
import ktsproject.composeapp.generated.resources.folder_icon
import ktsproject.composeapp.generated.resources.folder_icon_desc
import ktsproject.composeapp.generated.resources.repo_files_title
import ktsproject.composeapp.generated.resources.retry
import ktsproject.composeapp.generated.resources.sheets_leave_icon
import ktsproject.composeapp.generated.resources.sheets_leave_icon_desc
import ktsproject.composeapp.generated.resources.upload_confirm
import ktsproject.composeapp.generated.resources.upload_file_dialog_title
import ktsproject.composeapp.generated.resources.upload_file_icon
import ktsproject.composeapp.generated.resources.upload_file_icon_desc
import ktsproject.composeapp.generated.resources.upload_success
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val TEXT_EXTENSIONS = setOf(
    "kt", "kts", "java", "py", "js", "ts", "tsx", "jsx",
    "xml", "json", "yaml", "yml", "toml", "gradle",
    "md", "txt", "sh", "bash", "zsh", "fish",
    "html", "htm", "css", "scss", "sass",
    "c", "cpp", "h", "hpp", "rs", "go", "swift",
    "rb", "php", "cs", "dart", "sql", "r", "gitignore", "properties"
)

private fun GitHubContentItem.isTextFile(): Boolean {
    val ext = name.substringAfterLast(".", "").lowercase()
    return ext in TEXT_EXTENSIONS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoFilesScreen(
    viewModel: RepoFilesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSubdir: (repoName: String, ownerLogin: String, path: String) -> Unit,
    onNavigateToDescription: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val filePicker = rememberFilePicker { picked ->
        if (picked != null) viewModel.onFilePicked(picked)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painterResource(Res.drawable.arrow_back_icon),
                        contentDescription = stringResource(Res.string.back_icon),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = uiState.currentPath.ifEmpty {
                        stringResource(Res.string.repo_files_title)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
                IconButton(
                    onClick = onNavigateToDescription,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painterResource(Res.drawable.sheets_leave_icon),
                        contentDescription = stringResource(Res.string.sheets_leave_icon_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${stringResource(Res.string.error)}: ${uiState.error?.asString().orEmpty()}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = { viewModel.retry() }) {
                            Text(stringResource(Res.string.retry))
                        }
                    }
                }

                uiState.items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(Res.string.empty_directory),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = uiState.items,
                            key = { item -> item.path }
                        ) { item ->
                            ContentItem(
                                item = item,
                                onClick = {
                                    if (item.isDirectory) {
                                        onNavigateToSubdir(viewModel.repoName, viewModel.ownerLogin, item.path)
                                    } else if (item.isTextFile()) {
                                        viewModel.openFile(item)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { filePicker.launch() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painterResource(Res.drawable.upload_file_icon),
                contentDescription = stringResource(Res.string.upload_file_icon_desc)
            )
        }
    }

    if (uiState.showUploadDialog) {
        UploadDialog(
            status = uiState.uploadStatus,
            fileName = viewModel.pendingFileName,
            onConfirm = { viewModel.confirmUpload() },
            onDismiss = { viewModel.dismissUploadDialog() }
        )
    }

    if (uiState.selectedFile != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissFileSheet() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = uiState.selectedFile?.name.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                when {
                    uiState.isFileLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.fileError != null -> {
                        Text(
                            text = uiState.fileError?.asString().orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    uiState.fileContent != null -> {
                        Text(
                            text = uiState.fileContent.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UploadDialog(
    status: UploadStatus,
    fileName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (status !is UploadStatus.Uploading) onDismiss() },
        title = { Text(stringResource(Res.string.upload_file_dialog_title)) },
        text = {
            when (status) {
                is UploadStatus.Success -> {
                    Text(
                        text = stringResource(Res.string.upload_success),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is UploadStatus.Uploading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UploadStatus.Error -> {
                    Text(
                        text = status.message.asString(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is UploadStatus.Idle -> {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            if (status is UploadStatus.Success) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.cancel))
                }
            } else {
                TextButton(
                    onClick = onConfirm,
                    enabled = status is UploadStatus.Idle || status is UploadStatus.Error
                ) {
                    Text(stringResource(Res.string.upload_confirm))
                }
            }
        },
        dismissButton = {
            if (status !is UploadStatus.Success) {
                TextButton(
                    onClick = onDismiss,
                    enabled = status !is UploadStatus.Uploading
                ) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        }
    )
}

@Composable
private fun ContentItem(
    item: GitHubContentItem,
    onClick: () -> Unit
) {
    val isInteractive = item.isDirectory || item.isTextFile()
    Card(
        onClick = onClick,
        enabled = isInteractive,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    if (item.isDirectory) Res.drawable.folder_icon else Res.drawable.file_icon
                ),
                contentDescription = stringResource(
                    if (item.isDirectory) Res.string.folder_icon_desc else Res.string.file_icon_desc
                ),
                tint = if (item.isDirectory) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (item.isDirectory) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isInteractive) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.isFile && item.size > 0) {
                    Text(
                        text = formatFileSize(item.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (item.isFile && !item.isTextFile()) {
                    Text(
                        text = stringResource(Res.string.cannot_preview),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Int): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}

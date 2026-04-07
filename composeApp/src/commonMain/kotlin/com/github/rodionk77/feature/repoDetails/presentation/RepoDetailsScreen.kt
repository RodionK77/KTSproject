package com.github.rodionk77.feature.repoDetails.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.ImageData
import com.mikepenz.markdown.model.ImageTransformer
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.arrow_back_icon
import ktsproject.composeapp.generated.resources.back_icon
import ktsproject.composeapp.generated.resources.bookmark_icon
import ktsproject.composeapp.generated.resources.broken_image_icon
import ktsproject.composeapp.generated.resources.clear_icon
import ktsproject.composeapp.generated.resources.created_at
import ktsproject.composeapp.generated.resources.default_branch
import ktsproject.composeapp.generated.resources.error
import ktsproject.composeapp.generated.resources.forks
import ktsproject.composeapp.generated.resources.hourglass_icon
import ktsproject.composeapp.generated.resources.language
import ktsproject.composeapp.generated.resources.open_issues
import ktsproject.composeapp.generated.resources.private_repo
import ktsproject.composeapp.generated.resources.public_repo
import ktsproject.composeapp.generated.resources.bookmark_icon_desc
import ktsproject.composeapp.generated.resources.retry
import ktsproject.composeapp.generated.resources.stars
import ktsproject.composeapp.generated.resources.updated_at
import ktsproject.composeapp.generated.resources.visibility
import ktsproject.composeapp.generated.resources.watchers
import ktsproject.composeapp.generated.resources.сlose_image_desc
import ktsproject.composeapp.generated.resources.cancel
import ktsproject.composeapp.generated.resources.create_btn
import ktsproject.composeapp.generated.resources.create_issue
import ktsproject.composeapp.generated.resources.create_issue_desc
import ktsproject.composeapp.generated.resources.issue_body
import ktsproject.composeapp.generated.resources.issue_created_success
import ktsproject.composeapp.generated.resources.issue_title
import ktsproject.composeapp.generated.resources.file_icon_desc
import ktsproject.composeapp.generated.resources.folder_icon
import ktsproject.composeapp.generated.resources.report_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private class ReadmeImageTransformer(
    private val onImageClick: (String) -> Unit
) : ImageTransformer by Coil3ImageTransformerImpl {
    @Composable
    override fun transform(link: String): ImageData {
        val painter = rememberAsyncImagePainter(
            model = link,
            placeholder = painterResource(Res.drawable.hourglass_icon),
            error = painterResource(Res.drawable.broken_image_icon),
            contentScale = ContentScale.FillWidth
        )
        return ImageData(
            painter = painter,
            modifier = Modifier.fillMaxWidth().clickable { onImageClick(link) },
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun RepoDetailsScreen(
    viewModel: RepoDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToFiles: (repoName: String, ownerLogin: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val repo = uiState.repo
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
            if (repo != null) {
                Row {
                    IconButton(
                        onClick = { onNavigateToFiles(repo.name, repo.owner.login) },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painterResource(Res.drawable.folder_icon),
                            contentDescription = stringResource(Res.string.file_icon_desc),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painterResource(Res.drawable.bookmark_icon),
                            contentDescription = stringResource(Res.string.bookmark_icon_desc),
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = if (uiState.isFavorite) Modifier else Modifier.alpha(0.4f)
                        )
                    }
                }
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
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
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

            repo != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = repo.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (!repo.description.isNullOrBlank()) {
                        Text(
                            text = repo.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider()

                    if (!repo.language.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.language), repo.language)
                    }

                    RepoInfoRow(stringResource(Res.string.stars), repo.stargazersCount.toString())
                    RepoInfoRow(stringResource(Res.string.forks), repo.forksCount.toString())
                    RepoInfoRow(stringResource(Res.string.watchers), repo.watchersCount.toString())
                    RepoInfoRow(stringResource(Res.string.open_issues), repo.openIssuesCount.toString())

                    if (!repo.defaultBranch.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.default_branch), repo.defaultBranch)
                    }

                    if (!repo.createdAt.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.created_at), repo.createdAt)
                    }

                    if (!repo.updatedAt.isNullOrBlank()) {
                        RepoInfoRow(stringResource(Res.string.updated_at), repo.updatedAt)
                    }

                    RepoInfoRow(
                        label = stringResource(Res.string.visibility),
                        value = if (repo.private) stringResource(Res.string.private_repo)
                        else stringResource(Res.string.public_repo)
                    )

                    if (!uiState.readme.isNullOrBlank()) {
                        HorizontalDivider()
                        Markdown(
                            content = uiState.readme ?: "",
                            imageTransformer = remember { ReadmeImageTransformer { url -> selectedImageUrl = url } }
                        )
                    }

                }
            }
        }
    }
    if (repo != null) {
        FloatingActionButton(
            onClick = { viewModel.showCreateIssueDialog() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(
                painterResource(Res.drawable.report_icon),
                contentDescription = stringResource(Res.string.create_issue_desc)
            )
        }
    }
    }

    if (uiState.showCreateIssueDialog) {
        CreateIssueDialog(
            status = uiState.issueCreationStatus,
            onDismiss = { viewModel.dismissCreateIssueDialog() },
            onConfirm = { t, b -> viewModel.createIssue(t, b) }
        )
    }

    selectedImageUrl?.let { url ->
        FullscreenImageViewer(
            imageUrl = url,
            onDismiss = { selectedImageUrl = null }
        )
    }
}

@Composable
private fun CreateIssueDialog(
    status: IssueCreationStatus,
    onDismiss: () -> Unit,
    onConfirm: (title: String, body: String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var issueBody by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (status !is IssueCreationStatus.Sending) onDismiss() },
        title = { Text(stringResource(Res.string.create_issue)) },
        text = {
            when (status) {
                is IssueCreationStatus.Success -> {
                    Text(
                        stringResource(Res.string.issue_created_success),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(Res.string.issue_title)) },
                        singleLine = true,
                        enabled = status !is IssueCreationStatus.Sending,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = issueBody,
                        onValueChange = { issueBody = it },
                        label = { Text(stringResource(Res.string.issue_body)) },
                        minLines = 3,
                        enabled = status !is IssueCreationStatus.Sending,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (status is IssueCreationStatus.Error) {
                        Text(
                            text = status.message.asString(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (status is IssueCreationStatus.Sending) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        },
        confirmButton = {
            if (status is IssueCreationStatus.Success) {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
            } else {
                TextButton(
                    onClick = { onConfirm(title, issueBody) },
                    enabled = title.isNotBlank() && status !is IssueCreationStatus.Sending
                ) {
                    Text(stringResource(Res.string.create_btn))
                }
            }
        },
        dismissButton = {
            if (status !is IssueCreationStatus.Success) {
                TextButton(
                    onClick = onDismiss,
                    enabled = status !is IssueCreationStatus.Sending
                ) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        }
    )
}

@Composable
private fun RepoInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FullscreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offsetX += panChange.x
        offsetY += panChange.y
    }

    Dialog(
        onDismissRequest = {
            scale = 1f
            offsetX = 0f
            offsetY = 0f
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
                    .transformable(state = transformableState)
                    .clickable { onDismiss() },
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painterResource(Res.drawable.clear_icon),
                    contentDescription = stringResource(Res.string.сlose_image_desc),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

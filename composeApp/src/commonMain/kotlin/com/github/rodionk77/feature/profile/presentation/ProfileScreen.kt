package com.github.rodionk77.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.rodionk77.feature.profile.data.models.GitHubEventEntity
import io.github.aakira.napier.Napier
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.business_center_24px
import ktsproject.composeapp.generated.resources.calendar_icon
import ktsproject.composeapp.generated.resources.error
import ktsproject.composeapp.generated.resources.followers
import ktsproject.composeapp.generated.resources.following
import ktsproject.composeapp.generated.resources.history_icon
import ktsproject.composeapp.generated.resources.link_icon
import ktsproject.composeapp.generated.resources.location_icon
import ktsproject.composeapp.generated.resources.logout
import ktsproject.composeapp.generated.resources.mail_icon
import ktsproject.composeapp.generated.resources.member_since
import ktsproject.composeapp.generated.resources.no_recent_activity
import ktsproject.composeapp.generated.resources.recent_activity
import ktsproject.composeapp.generated.resources.repositories
import ktsproject.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { event ->
            when (event) {
                is ProfileUiEvent.NavigateToLogin -> onNavigateToLogin()
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

        else -> {
            val user = uiState.user
            Napier.d { user.toString() }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = user.login,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                if (!user.name.isNullOrBlank()) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!user.bio.isNullOrBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.repositories),
                        value = user.publicRepos.toString()
                    )
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.followers),
                        value = user.followers.toString()
                    )
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.following),
                        value = user.following.toString()
                    )
                }

                HorizontalDivider()

                if (!user.company.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.business_center_24px,
                        text = user.company
                    )
                }
                if (!user.location.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.location_icon,
                        text = user.location
                    )
                }
                if (!user.email.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.mail_icon,
                        text = user.email
                    )
                }
                if (!user.blog.isNullOrBlank()) {
                    ProfileInfoRow(
                        icon = Res.drawable.link_icon,
                        text = user.blog
                    )
                }
                if (!user.createdAt.isNullOrBlank()) {
                    val formattedDate = user.createdAt
                        .substringBefore("T")
                        .split("-")
                        .reversed()
                        .joinToString(".")

                    ProfileInfoRow(
                        icon = Res.drawable.calendar_icon,
                        text = "${stringResource(Res.string.member_since)}: $formattedDate"
                    )
                }

                HorizontalDivider()

                ActivitySection(
                    events = uiState.events,
                    isLoading = uiState.eventsLoading,
                    isPaginating = uiState.eventsPaginating,
                    onLoadMore = { viewModel.loadNextEventsPage() }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(Res.string.logout))
                }
            }
        }
    }
}

@Composable
private fun ActivitySection(
    events: List<GitHubEventEntity>,
    isLoading: Boolean,
    isPaginating: Boolean,
    onLoadMore: () -> Unit
) {
    Text(
        text = stringResource(Res.string.recent_activity),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )

    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }

        events.isEmpty() -> {
            Text(
                text = stringResource(Res.string.no_recent_activity),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        else -> {
            val listState = rememberLazyListState()

            val shouldLoadMore by remember {
                derivedStateOf {
                    val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    val total = listState.layoutInfo.totalItemsCount
                    lastVisible != null && lastVisible >= total - 3
                }
            }

            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = events,
                        key = { it.id }
                    ) { event ->
                        EventItem(event = event)
                    }

                    if (isPaginating) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventItem(event: GitHubEventEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.history_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatEventType(event.type),
                style = MaterialTheme.typography.bodyMedium
            )
            if (event.repo != null) {
                Text(
                    text = event.repo.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (event.createdAt != null) {
            Text(
                text = formatEventDate(event.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatEventType(type: String?): String {
    if (type == null) return "Event"
    return type
        .removeSuffix("Event")
        .replace(Regex("([a-z])([A-Z])"), "$1 $2")
}

private fun formatEventDate(isoDate: String): String {
    return isoDate
        .substringBefore("T")
        .split("-")
        .reversed()
        .joinToString(".")
}

@Composable
private fun ProfileStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileInfoRow(icon: DrawableResource, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

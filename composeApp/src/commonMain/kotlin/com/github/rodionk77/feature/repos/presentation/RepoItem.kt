package com.github.rodionk77.feature.repos.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.rodionk77.feature.repos.data.RepoEntity
import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.avatar
import ktsproject.composeapp.generated.resources.broken_image_icon
import ktsproject.composeapp.generated.resources.hourglass_icon
import ktsproject.composeapp.generated.resources.language
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RepoItem(repo: RepoEntity, avatar: String, onClick: () -> Unit) {
    Card(
        onClick =  onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (!repo.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = repo.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (!repo.language.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(Res.string.language)}: ${repo.language}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) //
            AsyncImage(
                model = avatar,
                contentDescription = stringResource(Res.string.avatar),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                error = painterResource(Res.drawable.broken_image_icon),
                placeholder = painterResource(Res.drawable.hourglass_icon),
                contentScale = ContentScale.Crop
            )
        }
    }
}
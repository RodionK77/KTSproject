package com.github.rodionk77.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) GitHubDarkColorScheme else GitHubLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

package com.github.rodionk77.common.Utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

class UnknownServerException : Exception()

class GitHubApiException(message: String) : Exception(message)

class TokenNotFoundException : Exception()


sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringRes(val resId: StringResource) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringRes -> stringResource(resId)
        }
    }

    suspend fun asSuspendString(): String {
        return when (this) {
            is DynamicString -> value
            is StringRes -> getString(resId)
        }
    }
}
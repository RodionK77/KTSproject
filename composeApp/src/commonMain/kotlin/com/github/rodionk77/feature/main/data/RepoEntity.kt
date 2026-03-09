package com.github.rodionk77.feature.main.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class RepoEntity(
    val id: Long,
    val name: String,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    val language: String? = null
)
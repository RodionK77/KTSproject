package com.github.rodionk77.feature.repos.data

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
    val language: String? = null,
    val owner: OwnerEntity
)

@Immutable
@Serializable
data class OwnerEntity(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)
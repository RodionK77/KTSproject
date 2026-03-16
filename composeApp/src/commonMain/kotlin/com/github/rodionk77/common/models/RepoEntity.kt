package com.github.rodionk77.common.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class RepoEntity(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("language") val language: String? = null,
    @SerialName("owner") val owner: OwnerEntity
)

@Immutable
@Serializable
data class OwnerEntity(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)
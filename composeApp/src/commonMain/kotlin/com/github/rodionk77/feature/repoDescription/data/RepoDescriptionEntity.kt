package com.github.rodionk77.feature.repoDescription.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class RepoDescriptionEntity(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("language") val language: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int = 0,
    @SerialName("forks_count") val forksCount: Int = 0,
    @SerialName("open_issues_count") val openIssuesCount: Int = 0,
    @SerialName("watchers_count") val watchersCount: Int = 0,
    @SerialName("default_branch") val defaultBranch: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("private") val private: Boolean = false,
    @SerialName("owner") val owner: OwnerEntity
)

@Immutable
@Serializable
data class OwnerEntity(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

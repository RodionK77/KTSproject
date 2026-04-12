package com.github.rodionk77.feature.profile.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubEventEntity(
    @SerialName("id")         val id: String,
    @SerialName("type")       val type: String? = null,
    @SerialName("repo")       val repo: EventRepo? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class EventRepo(
    @SerialName("name")       val name: String
)

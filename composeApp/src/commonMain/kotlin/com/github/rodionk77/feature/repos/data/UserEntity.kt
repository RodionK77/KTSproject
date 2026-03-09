package com.github.rodionk77.feature.repos.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("login") val login: String = "...",
    @SerialName("avatar_url") val avatarUrl: String = ""
)
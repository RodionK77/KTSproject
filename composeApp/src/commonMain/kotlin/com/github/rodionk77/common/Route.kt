package com.github.rodionk77.common

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Welcome : Route()

    @Serializable
    data class Login(val code: String? = null) : Route()

    @Serializable
    data object Repos : Route()

    @Serializable
    data class RepoDetails(val repoName: String, val ownerLogin: String) : Route()

    @Serializable
    data object Profile : Route()

    @Serializable
    data object Favorites : Route()

    @Serializable
    data class RepoFiles(
        val repoName: String,
        val ownerLogin: String,
        val path: String = ""
    ) : Route()
}
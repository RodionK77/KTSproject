package com.github.rodionk77.feature.repoDetails.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repo_descriptions")
data class RepoDescriptionDbEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val language: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val watchersCount: Int,
    val defaultBranch: String?,
    val updatedAt: String?,
    val createdAt: String?,
    val isPrivate: Boolean,
    val ownerLogin: String,
    val ownerAvatarUrl: String?,
    val readmeContent: String? = null
)
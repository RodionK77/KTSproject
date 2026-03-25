package com.github.rodionk77.feature.repoDetails.data.room

import com.github.rodionk77.feature.repoDetails.data.OwnerEntity
import com.github.rodionk77.feature.repoDetails.data.RepoDetailsEntity

fun RepoDetailsEntity.toDbEntity() = RepoDescriptionDbEntity(
    id = id, name = name, description = description, htmlUrl = htmlUrl,
    language = language, stargazersCount = stargazersCount, forksCount = forksCount,
    openIssuesCount = openIssuesCount, watchersCount = watchersCount,
    defaultBranch = defaultBranch, updatedAt = updatedAt, createdAt = createdAt,
    isPrivate = private, ownerLogin = owner.login, ownerAvatarUrl = owner.avatarUrl
)

fun RepoDescriptionDbEntity.toDomainEntity() = RepoDetailsEntity(
    id = id, name = name, description = description, htmlUrl = htmlUrl,
    language = language, stargazersCount = stargazersCount, forksCount = forksCount,
    openIssuesCount = openIssuesCount, watchersCount = watchersCount,
    defaultBranch = defaultBranch, updatedAt = updatedAt, createdAt = createdAt,
    private = isPrivate, owner = OwnerEntity(login = ownerLogin, avatarUrl = ownerAvatarUrl)
)
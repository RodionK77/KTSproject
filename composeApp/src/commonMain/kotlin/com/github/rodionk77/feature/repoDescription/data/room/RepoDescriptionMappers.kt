package com.github.rodionk77.feature.repoDescription.data.room

import com.github.rodionk77.feature.repoDescription.data.OwnerEntity
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionEntity

fun RepoDescriptionEntity.toDbEntity() = RepoDescriptionDbEntity(
    id = id, name = name, description = description, htmlUrl = htmlUrl,
    language = language, stargazersCount = stargazersCount, forksCount = forksCount,
    openIssuesCount = openIssuesCount, watchersCount = watchersCount,
    defaultBranch = defaultBranch, updatedAt = updatedAt, createdAt = createdAt,
    isPrivate = private, ownerLogin = owner.login, ownerAvatarUrl = owner.avatarUrl
)

fun RepoDescriptionDbEntity.toDomainEntity() = RepoDescriptionEntity(
    id = id, name = name, description = description, htmlUrl = htmlUrl,
    language = language, stargazersCount = stargazersCount, forksCount = forksCount,
    openIssuesCount = openIssuesCount, watchersCount = watchersCount,
    defaultBranch = defaultBranch, updatedAt = updatedAt, createdAt = createdAt,
    private = isPrivate, owner = OwnerEntity(login = ownerLogin, avatarUrl = ownerAvatarUrl)
)
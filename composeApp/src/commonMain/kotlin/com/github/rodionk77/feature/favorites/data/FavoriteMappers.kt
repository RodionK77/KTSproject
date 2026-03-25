package com.github.rodionk77.feature.favorites.data

import com.github.rodionk77.feature.repoDetails.data.RepoDetailsEntity
import com.github.rodionk77.common.models.OwnerEntity
import com.github.rodionk77.common.models.RepoEntity

fun FavoriteDbEntity.toRepoEntity() = RepoEntity(
    id = id,
    name = name,
    description = description,
    htmlUrl = htmlUrl,
    language = language,
    owner = OwnerEntity(login = ownerLogin, avatarUrl = ownerAvatarUrl)
)

fun RepoDetailsEntity.toFavoriteDbEntity() = FavoriteDbEntity(
    id = id,
    name = name,
    description = description,
    htmlUrl = htmlUrl,
    language = language,
    ownerLogin = owner.login,
    ownerAvatarUrl = owner.avatarUrl
)

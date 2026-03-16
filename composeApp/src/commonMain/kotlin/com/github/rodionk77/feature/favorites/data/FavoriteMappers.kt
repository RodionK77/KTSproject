package com.github.rodionk77.feature.favorites.data

import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionEntity
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

fun RepoDescriptionEntity.toFavoriteDbEntity() = FavoriteDbEntity(
    id = id,
    name = name,
    description = description,
    htmlUrl = htmlUrl,
    language = language,
    ownerLogin = owner.login,
    ownerAvatarUrl = owner.avatarUrl
)

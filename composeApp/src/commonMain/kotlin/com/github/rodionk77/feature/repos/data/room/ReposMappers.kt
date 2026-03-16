package com.github.rodionk77.feature.repos.data.room

import com.github.rodionk77.common.models.OwnerEntity
import com.github.rodionk77.common.models.RepoEntity
import com.github.rodionk77.common.models.UserEntity

fun RepoEntity.toDbEntity() = RepoDbEntity(
    id = id, name = name, description = description,
    htmlUrl = htmlUrl, language = language,
    ownerLogin = owner.login, ownerAvatarUrl = owner.avatarUrl
)

fun RepoDbEntity.toDomainEntity() = RepoEntity(
    id = id, name = name, description = description,
    htmlUrl = htmlUrl, language = language,
    owner = OwnerEntity(login = ownerLogin, avatarUrl = ownerAvatarUrl)
)

fun UserEntity.toDbEntity() = UserDbEntity(
    login = login,
    avatarUrl = avatarUrl,
    name = name,
    bio = bio,
    email = email,
    location = location,
    company = company,
    blog = blog,
    twitterUsername = twitterUsername,
    publicRepos = publicRepos,
    followers = followers,
    following = following,
    createdAt = createdAt
)

fun UserDbEntity.toDomainEntity() = UserEntity(
    login = login,
    avatarUrl = avatarUrl,
    name = name,
    bio = bio,
    email = email,
    location = location,
    company = company,
    blog = blog,
    twitterUsername = twitterUsername,
    publicRepos = publicRepos,
    followers = followers,
    following = following,
    createdAt = createdAt
)
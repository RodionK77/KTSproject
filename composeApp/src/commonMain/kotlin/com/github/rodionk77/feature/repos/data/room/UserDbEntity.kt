package com.github.rodionk77.feature.repos.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserDbEntity(
    @PrimaryKey val id: Int = 0,
    val login: String,
    val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val email: String?,
    val location: String?,
    val company: String?,
    val blog: String?,
    val twitterUsername: String?,
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    val createdAt: String?
)
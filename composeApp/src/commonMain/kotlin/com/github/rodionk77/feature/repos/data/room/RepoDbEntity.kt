package com.github.rodionk77.feature.repos.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoDbEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val language: String?,
    val ownerLogin: String,
    val ownerAvatarUrl: String?
)
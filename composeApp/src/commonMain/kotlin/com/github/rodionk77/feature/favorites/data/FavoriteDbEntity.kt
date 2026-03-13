package com.github.rodionk77.feature.favorites.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteDbEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val language: String?,
    val ownerLogin: String,
    val ownerAvatarUrl: String?
)

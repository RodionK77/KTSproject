package com.github.rodionk77.feature.favorites.domain

import com.github.rodionk77.common.models.RepoEntity
import com.github.rodionk77.feature.repoDetails.data.RepoDetailsEntity

interface FavoritesRepository {
    suspend fun getFavorites(): List<RepoEntity>
    suspend fun addFavorite(repo: RepoDetailsEntity)
    suspend fun removeFavorite(id: Long)
    suspend fun isFavorite(id: Long): Boolean
}
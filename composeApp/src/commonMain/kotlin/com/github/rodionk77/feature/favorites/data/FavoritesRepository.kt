package com.github.rodionk77.feature.favorites.data

import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionEntity
import com.github.rodionk77.common.models.RepoEntity

class FavoritesRepository(private val dao: FavoriteDao) {

    suspend fun getFavorites(): List<RepoEntity> =
        dao.getAll().map { it.toRepoEntity() }

    suspend fun addFavorite(repo: RepoDescriptionEntity) =
        dao.upsert(repo.toFavoriteDbEntity())

    suspend fun removeFavorite(id: Long) =
        dao.deleteById(id)

    suspend fun isFavorite(id: Long): Boolean =
        dao.getById(id) != null
}

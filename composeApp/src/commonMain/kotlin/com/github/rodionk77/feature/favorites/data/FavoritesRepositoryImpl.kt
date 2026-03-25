package com.github.rodionk77.feature.favorites.data

import com.github.rodionk77.feature.repoDetails.data.RepoDetailsEntity
import com.github.rodionk77.common.models.RepoEntity
import com.github.rodionk77.feature.favorites.domain.FavoritesRepository

class FavoritesRepositoryImpl(private val dao: FavoriteDao) : FavoritesRepository {

    override suspend fun getFavorites(): List<RepoEntity> =
        dao.getAll().map { it.toRepoEntity() }

    override suspend fun addFavorite(repo: RepoDetailsEntity) =
        dao.upsert(repo.toFavoriteDbEntity())

    override suspend fun removeFavorite(id: Long) =
        dao.deleteById(id)

    override suspend fun isFavorite(id: Long): Boolean =
        dao.getById(id) != null
}

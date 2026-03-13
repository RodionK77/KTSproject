package com.github.rodionk77.feature.favorites.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    suspend fun getAll(): List<FavoriteDbEntity>

    @Upsert
    suspend fun upsert(repo: FavoriteDbEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getById(id: Long): FavoriteDbEntity?

    @Query("DELETE FROM favorites")
    suspend fun clearAll()
}

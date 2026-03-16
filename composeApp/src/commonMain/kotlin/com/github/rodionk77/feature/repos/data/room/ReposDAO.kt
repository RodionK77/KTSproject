package com.github.rodionk77.feature.repos.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ReposDao {
    @Query("SELECT * FROM repos")
    suspend fun getAll(): List<RepoDbEntity>

    @Upsert
    suspend fun upsertAll(repos: List<RepoDbEntity>)

    @Query("DELETE FROM repos")
    suspend fun clearAll()
}
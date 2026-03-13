package com.github.rodionk77.feature.repoDescription.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RepoDescriptionDao {
    @Query("SELECT * FROM repo_descriptions WHERE name = :repoName AND ownerLogin = :ownerLogin")
    suspend fun get(repoName: String, ownerLogin: String): RepoDescriptionDbEntity?

    @Upsert
    suspend fun upsert(repo: RepoDescriptionDbEntity)

    @Query("DELETE FROM repo_descriptions")
    suspend fun clearAll()
}
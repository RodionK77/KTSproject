package com.github.rodionk77.feature.repoDetails.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RepoDescriptionDao {
    @Query("SELECT * FROM repo_descriptions WHERE name = :repoName AND ownerLogin = :ownerLogin")
    suspend fun get(repoName: String, ownerLogin: String): RepoDescriptionDbEntity?

    @Upsert
    suspend fun upsert(repo: RepoDescriptionDbEntity)

    @Query("UPDATE repo_descriptions SET readmeContent = :content WHERE name = :repoName AND ownerLogin = :ownerLogin")
    suspend fun updateReadme(repoName: String, ownerLogin: String, content: String)

    @Query("DELETE FROM repo_descriptions")
    suspend fun clearAll()
}
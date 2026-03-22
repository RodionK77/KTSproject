package com.github.rodionk77.feature.repos.domain

import com.github.rodionk77.common.models.RepoEntity
import com.github.rodionk77.common.models.UserEntity

interface ReposRepository {
    suspend fun getRepositories(page: Int, perPage: Int = 20, useCache: Boolean = true): Result<List<RepoEntity>>
    suspend fun getProfile(): Result<UserEntity>
}
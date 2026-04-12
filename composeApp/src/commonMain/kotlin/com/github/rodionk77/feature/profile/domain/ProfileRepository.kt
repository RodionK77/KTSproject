package com.github.rodionk77.feature.profile.domain

import com.github.rodionk77.common.models.UserEntity
import com.github.rodionk77.feature.profile.data.models.GitHubEventEntity

interface ProfileRepository {
    suspend fun getProfile(): Result<UserEntity>
    suspend fun getCachedProfile(): UserEntity?
    suspend fun getEvents(username: String, page: Int = 1, perPage: Int = 10): Result<List<GitHubEventEntity>>
    suspend fun logout()
}
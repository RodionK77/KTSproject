package com.github.rodionk77.feature.profile.domain

import com.github.rodionk77.common.models.UserEntity

interface ProfileRepository {
    suspend fun getProfile(): Result<UserEntity>
    suspend fun getCachedProfile(): UserEntity?
    suspend fun logout()
}
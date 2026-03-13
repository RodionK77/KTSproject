package com.github.rodionk77.feature.repos.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = 0")
    suspend fun getUser(): UserDbEntity?

    @Upsert
    suspend fun upsertUser(user: UserDbEntity)

    @Query("DELETE FROM user")
    suspend fun clearAll()
}
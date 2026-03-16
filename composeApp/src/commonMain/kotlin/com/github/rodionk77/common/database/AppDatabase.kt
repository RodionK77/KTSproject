package com.github.rodionk77.common.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Transaction
import com.github.rodionk77.feature.favorites.data.FavoriteDao
import com.github.rodionk77.feature.favorites.data.FavoriteDbEntity
import com.github.rodionk77.feature.repoDescription.data.room.RepoDescriptionDao
import com.github.rodionk77.feature.repoDescription.data.room.RepoDescriptionDbEntity
import com.github.rodionk77.feature.repos.data.room.RepoDbEntity
import com.github.rodionk77.feature.repos.data.room.ReposDao
import com.github.rodionk77.feature.repos.data.room.UserDao
import com.github.rodionk77.feature.repos.data.room.UserDbEntity

@Database(
    entities = [RepoDbEntity::class, UserDbEntity::class, RepoDescriptionDbEntity::class, FavoriteDbEntity::class],
    version = 3
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reposDao(): ReposDao
    abstract fun userDao(): UserDao
    abstract fun repoDescriptionDao(): RepoDescriptionDao
    abstract fun favoriteDao(): FavoriteDao

    @Transaction
    open suspend fun clearAllData() {
        reposDao().clearAll()
        userDao().clearAll()
        repoDescriptionDao().clearAll()
        favoriteDao().clearAll()
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
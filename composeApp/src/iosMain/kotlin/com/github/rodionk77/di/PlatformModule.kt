package com.github.rodionk77.di

import com.github.rodionk77.common.database.AppDatabase
import com.github.rodionk77.common.database.getRoomDatabase
import com.github.rodionk77.getDatabaseBuilder
import com.liftric.kvault.KVault
import org.koin.dsl.module

val iosPlatformModule = module {
    single<KVault> { KVault() }
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}

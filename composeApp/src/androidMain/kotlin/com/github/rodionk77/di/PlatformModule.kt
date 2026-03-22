package com.github.rodionk77.di

import com.github.rodionk77.common.database.AppDatabase
import com.github.rodionk77.common.database.getRoomDatabase
import com.github.rodionk77.getDatabaseBuilder
import com.liftric.kvault.KVault
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule = module {
    single<KVault> { KVault(context = androidContext()) }
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder(androidContext())) }
}

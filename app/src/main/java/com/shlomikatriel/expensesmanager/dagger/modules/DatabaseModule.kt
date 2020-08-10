package com.shlomikatriel.expensesmanager.dagger.modules

import android.content.Context
import androidx.room.Room
import com.shlomikatriel.expensesmanager.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(appContext: Context) = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, AppDatabase.DB_NAME
    ).build()

    @Provides
    @Singleton
    fun provideExpenseDao(appDatabase: AppDatabase) = appDatabase.expenseDao()
}
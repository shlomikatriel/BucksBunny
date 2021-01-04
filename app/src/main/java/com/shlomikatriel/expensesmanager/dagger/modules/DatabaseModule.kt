package com.shlomikatriel.expensesmanager.dagger.modules

import android.content.Context
import androidx.room.Room
import com.shlomikatriel.expensesmanager.database.AppDatabase
import com.shlomikatriel.expensesmanager.database.DatabaseMigrations
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(appContext: Context, databaseMigrations: DatabaseMigrations) = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, AppDatabase.DB_NAME
    ).addMigrations(*databaseMigrations.getAllMigrations())
        .build()

    @Provides
    @Singleton
    fun provideOneTimeExpenseDao(appDatabase: AppDatabase) = appDatabase.oneTimeExpenseDao()

    @Provides
    @Singleton
    fun provideMonthlyExpenseDao(appDatabase: AppDatabase) = appDatabase.monthlyExpenseDao()

    @Provides
    @Singleton
    fun providePaymentsExpenseDao(appDatabase: AppDatabase) = appDatabase.paymentsExpenseDao()
}
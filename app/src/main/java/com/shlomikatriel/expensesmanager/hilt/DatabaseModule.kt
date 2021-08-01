package com.shlomikatriel.expensesmanager.hilt

import android.content.Context
import androidx.room.Room
import com.shlomikatriel.expensesmanager.database.AppDatabase
import com.shlomikatriel.expensesmanager.database.DatabaseMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context, databaseMigrations: DatabaseMigrations) = Room.databaseBuilder(
        context,
        AppDatabase::class.java, AppDatabase.DB_NAME
    ).addMigrations(*databaseMigrations.getAllMigrations())
        .build()

    @Provides
    fun provideOneTimeExpenseDao(appDatabase: AppDatabase) = appDatabase.oneTimeExpenseDao()

    @Provides
    fun provideMonthlyExpenseDao(appDatabase: AppDatabase) = appDatabase.monthlyExpenseDao()

    @Provides
    fun providePaymentsExpenseDao(appDatabase: AppDatabase) = appDatabase.paymentsExpenseDao()
}
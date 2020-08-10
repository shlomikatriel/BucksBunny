package com.shlomikatriel.expensesmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "expense_manager.db"
    }

    abstract fun expenseDao(): ExpenseDao
}
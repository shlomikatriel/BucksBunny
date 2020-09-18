package com.shlomikatriel.expensesmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shlomikatriel.expensesmanager.database.dao.MonthlyExpenseDao
import com.shlomikatriel.expensesmanager.database.dao.OneTimeExpenseDao
import com.shlomikatriel.expensesmanager.database.dao.PaymentsExpenseDao
import com.shlomikatriel.expensesmanager.database.model.MonthlyExpenseModel
import com.shlomikatriel.expensesmanager.database.model.OneTimeExpenseModel
import com.shlomikatriel.expensesmanager.database.model.PaymentsExpenseModel

@Database(
    entities = [
        OneTimeExpenseModel::class,
        MonthlyExpenseModel::class,
        PaymentsExpenseModel::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "expense_manager.db"
    }

    abstract fun oneTimeExpenseDao(): OneTimeExpenseDao

    abstract fun monthlyExpenseDao(): MonthlyExpenseDao

    abstract fun paymentsExpenseDao(): PaymentsExpenseDao


}
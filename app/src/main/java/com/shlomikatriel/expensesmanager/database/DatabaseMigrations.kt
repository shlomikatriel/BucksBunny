package com.shlomikatriel.expensesmanager.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shlomikatriel.expensesmanager.logs.logInfo
import javax.inject.Inject

class DatabaseMigrations
@Inject constructor() {
    private val from1To2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            logInfo("Migrating database 1 -> 2")
            database.apply {
                // Create new tables
                execSQL("CREATE TABLE IF NOT EXISTS one_time_expense (id INTEGER PRIMARY KEY AUTOINCREMENT, month INTEGER NOT NULL, time_stamp INTEGER NOT NULL, name TEXT NOT NULL, cost REAL NOT NULL)")
                execSQL("CREATE TABLE IF NOT EXISTS monthly_expense (id INTEGER PRIMARY KEY AUTOINCREMENT, time_stamp INTEGER NOT NULL, name TEXT NOT NULL, cost REAL NOT NULL)")
                execSQL("CREATE TABLE IF NOT EXISTS payments_expense (id INTEGER PRIMARY KEY AUTOINCREMENT, month INTEGER NOT NULL, payments INTEGER NOT NULL, time_stamp INTEGER NOT NULL, name TEXT NOT NULL, cost REAL NOT NULL)")

                // Data migration
                execSQL("INSERT INTO one_time_expense(id, month, time_stamp, name, cost) SELECT id, year * 12 + month, time_stamp, name, amount FROM expense WHERE is_monthly = 0")
                execSQL("INSERT INTO monthly_expense(id, time_stamp, name, cost) SELECT id, time_stamp, name, amount FROM expense WHERE is_monthly = 1")

                // Remove old table
                execSQL("DROP TABLE IF EXISTS expense")
            }
            logInfo("Database 1 -> 2  migration complete")
        }
    }

    fun getAllMigrations() = arrayOf(from1To2)
}


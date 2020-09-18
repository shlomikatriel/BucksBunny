package com.shlomikatriel.expensesmanager

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shlomikatriel.expensesmanager.database.AppDatabase
import com.shlomikatriel.expensesmanager.database.DatabaseMigrations
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    private val migrations = DatabaseMigrations()

    @Rule
    @JvmField
    var helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        createDatabase(1).apply {
            execSQL("INSERT INTO expense(time_stamp,name,amount,is_monthly,year,month) VALUES (11,'TV',1500.0,0,2020,12)")
            execSQL("INSERT INTO expense(time_stamp,name,amount,is_monthly,year,month) VALUES (22,'Netflix',60.0,1,NULL,NULL)")
            execSQL("INSERT INTO expense(time_stamp,name,amount,is_monthly,year,month) VALUES (33,'Cables',20.0,1,2020,12)")
            close()
        }

        migrateDatabase(1, 2).apply {
            query("SELECT * FROM one_time_expense WHERE time_stamp=11").apply {
                moveToFirst()
                assertRowCount(1)
                assertColumnCount(5)
                assertColumnValue("time_stamp", 11)
                assertColumnValue("name", "TV")
                assertColumnValue("cost", 1500.0f)
                assertColumnValue("month", 24252)
            }

            query("SELECT * FROM monthly_expense WHERE time_stamp=22").apply {
                moveToFirst()
                assertRowCount(1)
                assertColumnCount(4)
                assertColumnValue("time_stamp", 22)
                assertColumnValue("name", "Netflix")
                assertColumnValue("cost", 60.0f)
            }

            query("SELECT * FROM monthly_expense WHERE time_stamp=33").apply {
                moveToFirst()
                assertRowCount(1)
                assertColumnCount(4)
                assertColumnValue("time_stamp", 33)
                assertColumnValue("name", "Cables")
                assertColumnValue("cost", 20.0f)
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun createDatabase(version: Int) = helper.createDatabase(TEST_DB, version)

    @Suppress("SameParameterValue")
    private fun migrateDatabase(from: Int, to: Int) = helper.runMigrationsAndValidate(
        TEST_DB,
        to,
        true,
        migrations.getAllMigrations()[from - 1]
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB
        ).addMigrations(*migrations.getAllMigrations()).build().apply {
            openHelper.writableDatabase
            close()
        }
    }

    companion object {
        private const val TEST_DB = "migration-test"
    }
}
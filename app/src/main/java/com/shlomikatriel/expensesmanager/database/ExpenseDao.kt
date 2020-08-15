package com.shlomikatriel.expensesmanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expense WHERE month=:month AND year=:year OR is_monthly=1")
    fun getExpensesOfMonth(month: Int, year: Int): LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE id=:id")
    fun getExpenseById(id: Long): Expense

    @Insert
    fun insert(expense: Expense)

    @Update
    fun update(expense: Expense)

    @Query("DELETE FROM expense WHERE id=:id")
    fun deleteById(id: Long)
}
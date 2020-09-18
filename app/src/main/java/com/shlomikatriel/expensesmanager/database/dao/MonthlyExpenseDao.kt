package com.shlomikatriel.expensesmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.shlomikatriel.expensesmanager.database.model.MonthlyExpenseModel

@Dao
interface MonthlyExpenseDao : BaseDao<MonthlyExpenseModel> {

    @Query("SELECT * FROM monthly_expense")
    fun getMonthlyExpenses(): LiveData<List<MonthlyExpenseModel>>

    @Query("SELECT * FROM monthly_expense where :id=id")
    fun getExpenseById(id: Long): MonthlyExpenseModel
}
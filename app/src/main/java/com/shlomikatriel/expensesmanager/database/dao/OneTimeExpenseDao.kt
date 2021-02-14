package com.shlomikatriel.expensesmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.shlomikatriel.expensesmanager.database.model.OneTimeExpenseModel

@Dao
interface OneTimeExpenseDao : BaseDao<OneTimeExpenseModel> {

    /**
     * Month value is the months that passed since year 0
     * */
    @Query("SELECT * FROM one_time_expense WHERE month=:month")
    fun getExpensesOfMonth(month: Int): LiveData<List<OneTimeExpenseModel>>

    @Query("SELECT * FROM one_time_expense where :id=id")
    fun getExpenseById(id: Long): OneTimeExpenseModel

    @Query("SELECT COUNT(*) FROM one_time_expense")
    fun count(): Int
}
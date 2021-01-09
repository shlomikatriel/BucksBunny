package com.shlomikatriel.expensesmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.shlomikatriel.expensesmanager.database.model.PaymentsExpenseModel

@Dao
interface PaymentsExpenseDao : BaseDao<PaymentsExpenseModel> {

    /**
     * Month value is the months that passed since year 0
     * */
    @Query("SELECT * FROM payments_expense WHERE month BETWEEN :month - payments + 1 AND :month")
    fun getExpensesOfMonth(month: Int): LiveData<List<PaymentsExpenseModel>>

    @Query("SELECT * FROM payments_expense where :id=id")
    fun getExpenseById(id: Long): PaymentsExpenseModel

    @Query("SELECT COUNT(*) FROM payments_expense")
    fun count(): Int
}
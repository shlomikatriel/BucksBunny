package com.shlomikatriel.expensesmanager.database

import androidx.annotation.Keep
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.shlomikatriel.expensesmanager.database.dao.MonthlyExpenseDao
import com.shlomikatriel.expensesmanager.database.dao.OneTimeExpenseDao
import com.shlomikatriel.expensesmanager.database.dao.PaymentsExpenseDao
import com.shlomikatriel.expensesmanager.database.model.*
import com.shlomikatriel.expensesmanager.logs.Logger
import javax.inject.Inject

@Keep
class DatabaseManager
@Inject constructor() {

    @Inject
    lateinit var oneTimeExpenseDao: OneTimeExpenseDao

    @Inject
    lateinit var monthlyExpenseDao: MonthlyExpenseDao

    @Inject
    lateinit var paymentsExpenseDao: PaymentsExpenseDao

    @WorkerThread
    fun insert(expense: Expense) {
        Logger.d("Inserting expense: $expense")
        when (expense) {
            is Expense.OneTime -> oneTimeExpenseDao.insert(expense.toModel())
            is Expense.Monthly -> monthlyExpenseDao.insert(expense.toModel())
            is Expense.Payments -> paymentsExpenseDao.insert(expense.toModel())
        }
    }

    @WorkerThread
    fun update(expense: Expense) {
        if (expense.databaseId == null) {
            Logger.w("Can't update expense with no id")
            return
        }
        Logger.d("Updating expense: $expense")
        when (expense) {
            is Expense.OneTime -> oneTimeExpenseDao.update(expense.toModel())
            is Expense.Monthly -> monthlyExpenseDao.update(expense.toModel())
            is Expense.Payments -> paymentsExpenseDao.update(expense.toModel())
        }
    }

    fun delete(expense: Expense) {
        Logger.d("Deleting expense: $expense")
        when (expense) {
            is Expense.OneTime -> oneTimeExpenseDao.delete(expense.toModel())
            is Expense.Monthly -> monthlyExpenseDao.delete(expense.toModel())
            is Expense.Payments -> paymentsExpenseDao.delete(expense.toModel())
        }
    }

    @WorkerThread
    fun getExpense(id: Long, type: ExpenseType): Expense {
        Logger.d("Selecting expense id $id from type $type")
        return when (type) {
            ExpenseType.ONE_TIME -> oneTimeExpenseDao.getExpenseById(id).toExpense()
            ExpenseType.MONTHLY -> monthlyExpenseDao.getExpenseById(id).toExpense()
            ExpenseType.PAYMENTS -> paymentsExpenseDao.getExpenseById(id).toExpense()
        }
    }

    @UiThread
    fun getExpensesOfMonth(month: Int) = MediatorLiveData<List<Expense>>().apply {
        attachExpensesLiveDataSource(
            Expense.OneTime::class.java,
            { oneTimeExpenseDao.getExpensesOfMonth(month) },
            { it.toExpense() }
        )
        attachExpensesLiveDataSource(
            Expense.Monthly::class.java,
            { monthlyExpenseDao.getMonthlyExpenses() },
            { it.toExpense() }
        )
        attachExpensesLiveDataSource(
            Expense.Payments::class.java,
            { paymentsExpenseDao.getExpensesOfMonth(month) },
            { it.toExpense() }
        )
    }

    private fun <M, E : Expense> MediatorLiveData<List<Expense>>.attachExpensesLiveDataSource(
        expenseClass: Class<E>,
        getExpenses: () -> LiveData<List<M>>,
        convertToExpense: (M) -> Expense
    ) {
        addSource(getExpenses()) { models ->
            synchronized(this) {
                val newExpenses = mutableListOf<Expense>()
                newExpenses.addAll(models.map { convertToExpense(it) })

                val oldExpenses = value
                if (oldExpenses != null) {
                    newExpenses.addAll(oldExpenses.filter { it.javaClass != expenseClass })
                }

                value = newExpenses
            }
        }
    }
}
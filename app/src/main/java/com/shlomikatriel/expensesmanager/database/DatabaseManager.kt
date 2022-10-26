package com.shlomikatriel.expensesmanager.database

import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.shlomikatriel.expensesmanager.database.dao.MonthlyExpenseDao
import com.shlomikatriel.expensesmanager.database.dao.OneTimeExpenseDao
import com.shlomikatriel.expensesmanager.database.dao.PaymentsExpenseDao
import com.shlomikatriel.expensesmanager.database.model.MonthlyExpenseModel
import com.shlomikatriel.expensesmanager.database.model.OneTimeExpenseModel
import com.shlomikatriel.expensesmanager.database.model.PaymentsExpenseModel
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logWarning
import javax.inject.Inject

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
        logDebug("Inserting expense: $expense")
        when (expense) {
            is Expense.OneTime -> oneTimeExpenseDao.insert(expense.toModel())
            is Expense.Monthly -> monthlyExpenseDao.insert(expense.toModel())
            is Expense.Payments -> paymentsExpenseDao.insert(expense.toModel())
        }
    }

    @WorkerThread
    fun update(expense: Expense) {
        if (expense.databaseId == null) {
            logWarning("Can't update expense with no id")
            return
        }
        logDebug("Updating expense: $expense")
        when (expense) {
            is Expense.OneTime -> oneTimeExpenseDao.update(expense.toModel())
            is Expense.Monthly -> monthlyExpenseDao.update(expense.toModel())
            is Expense.Payments -> paymentsExpenseDao.update(expense.toModel())
        }
    }

    fun delete(expense: Expense) {
        logDebug("Deleting expense: $expense")
        when (expense) {
            is Expense.OneTime -> oneTimeExpenseDao.delete(expense.toModel())
            is Expense.Monthly -> monthlyExpenseDao.delete(expense.toModel())
            is Expense.Payments -> paymentsExpenseDao.delete(expense.toModel())
        }
    }

    @UiThread
    fun getExpensesOfMonth(month: Int) = MediatorLiveData<ArrayList<Expense>>().apply {
        addSource(oneTimeExpenseDao.getExpensesOfMonth(month).asOneTimeExpenseList()) {
            synchronized(this) {
                updateExpenses(it, Expense.OneTime::class.java)
            }
        }
        addSource(monthlyExpenseDao.getMonthlyExpenses().asMonthlyExpenseList()) {
            synchronized(this) {
                updateExpenses(it, Expense.Monthly::class.java)
            }
        }
        addSource(paymentsExpenseDao.getExpensesOfMonth(month).asPaymentsExpenseList()) {
            synchronized(this) {
                updateExpenses(it, Expense.Payments::class.java)
            }
        }
    }

    fun countExpenses(): Int {
        val oneTime = oneTimeExpenseDao.count()
        val monthly = monthlyExpenseDao.count()
        val payments = paymentsExpenseDao.count()
        logDebug("Counting expenses [oneTime=$oneTime, monthly=$monthly, payments=$payments]")
        return oneTime + monthly + payments
    }

    private fun <E : Expense> MediatorLiveData<ArrayList<Expense>>.updateExpenses(expenses: List<Expense>, expenseClass: Class<E>) {
        val newExpenses = arrayListOf<Expense>()
        newExpenses.addAll(expenses)

        val oldExpenses = value
        if (oldExpenses != null) {
            newExpenses.addAll(oldExpenses.filter { it.javaClass != expenseClass })
        }
        value = newExpenses
    }

    private fun LiveData<List<OneTimeExpenseModel>>.asOneTimeExpenseList(): LiveData<List<Expense>> {
        return Transformations.map(this) { modelList ->
            modelList.map { it.toExpense() }
        }
    }

    private fun LiveData<List<MonthlyExpenseModel>>.asMonthlyExpenseList(): LiveData<List<Expense>> {
        return Transformations.map(this) { modelList ->
            modelList.map { it.toExpense() }
        }
    }

    private fun LiveData<List<PaymentsExpenseModel>>.asPaymentsExpenseList(): LiveData<List<Expense>> {
        return Transformations.map(this) { modelList ->
            modelList.map { it.toExpense() }
        }
    }
}
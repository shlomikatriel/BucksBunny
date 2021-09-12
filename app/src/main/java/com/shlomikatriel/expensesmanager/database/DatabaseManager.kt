package com.shlomikatriel.expensesmanager.database

import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.shlomikatriel.expensesmanager.LocalizationManager
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

    @Inject
    lateinit var localizationManager: LocalizationManager

    @WorkerThread
    fun insert(expenseModel: OneTimeExpenseModel) {
        logDebug("Inserting expense: $expenseModel")
        oneTimeExpenseDao.insert(expenseModel)
    }

    @WorkerThread
    fun insert(expenseModel: MonthlyExpenseModel) {
        logDebug("Inserting expense: $expenseModel")
        monthlyExpenseDao.insert(expenseModel)
    }

    @WorkerThread
    fun insert(expenseModel: PaymentsExpenseModel) {
        logDebug("Inserting expense: $expenseModel")
        paymentsExpenseDao.insert(expenseModel)
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
        val format = localizationManager.getCurrencyFormat()
        attachExpensesLiveDataSource(
            Expense.OneTime::class.java,
            { oneTimeExpenseDao.getExpensesOfMonth(month) },
            { it.toExpense(format) }
        )
        attachExpensesLiveDataSource(
            Expense.Monthly::class.java,
            { monthlyExpenseDao.getMonthlyExpenses() },
            { it.toExpense(format) }
        )
        attachExpensesLiveDataSource(
            Expense.Payments::class.java,
            { paymentsExpenseDao.getExpensesOfMonth(month) },
            { it.toExpense(format) }
        )
    }

    private fun <M, E : Expense> MediatorLiveData<ArrayList<Expense>>.attachExpensesLiveDataSource(
        expenseClass: Class<E>,
        getExpenses: () -> LiveData<List<M>>,
        convertToExpense: (M) -> Expense
    ) {
        addSource(getExpenses()) { models ->
            synchronized(this) {
                val newExpenses = arrayListOf<Expense>()
                newExpenses.addAll(models.map { convertToExpense(it) })

                val oldExpenses = value
                if (oldExpenses != null) {
                    newExpenses.addAll(oldExpenses.filter { it.javaClass != expenseClass })
                }

                value = newExpenses
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
}
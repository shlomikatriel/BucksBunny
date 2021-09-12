package com.shlomikatriel.expensesmanager.expenses.mvi

import android.content.SharedPreferences
import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat

abstract class ExpensesBaseViewModel(
    private val databaseManager: DatabaseManager,
    protected val sharedPreferences: SharedPreferences
) : ViewModel(), Observer<ArrayList<Expense>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private var expenseItemsLiveData: LiveData<ArrayList<Expense>>? = null

    fun observeChanges(month: Int) {
        expenseItemsLiveData?.removeObserver(this)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        expenseItemsLiveData = databaseManager.getExpensesOfMonth(month).apply {
            observeForever(this@ExpensesBaseViewModel)
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onChanged(expenses: ArrayList<Expense>?) {
        expenses?.let { onExpensesChanged(it) }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == FloatKey.INCOME.getKey()) {
            onIncomeChanged(this.sharedPreferences.getFloat(FloatKey.INCOME))
        }
    }

    abstract fun onIncomeChanged(income: Float)

    abstract fun onExpensesChanged(expenses: ArrayList<Expense>)

    protected fun getExpenses() =
        expenseItemsLiveData?.value ?: arrayListOf()

    protected fun calculateTotal(expenses: ArrayList<Expense>) = expenses.sumOf {
        when (it) {
            is Expense.OneTime, is Expense.Monthly -> it.cost.toDouble()
            is Expense.Payments -> (it.cost / it.payments).toDouble()
        }
    }

    @CallSuper
    override fun onCleared() {
        logInfo("View model cleared")
        expenseItemsLiveData?.removeObserver(this)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


}
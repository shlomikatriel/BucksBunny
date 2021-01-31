package com.shlomikatriel.expensesmanager.ui.expenses.mvi

import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import javax.inject.Inject

abstract class ExpensesBaseViewModel(
        application: Application
) : AndroidViewModel(application), Observer<ArrayList<Expense>>, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var databaseManager: DatabaseManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
        logInfo("Expenses changed")
        expenses?.let { onExpensesChanged(it.transformToArrayList()) }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == FloatKey.INCOME.getKey()) {
            logInfo("Income changed")
            onIncomeChanged(this.sharedPreferences.getFloat(FloatKey.INCOME))
        }
    }

    abstract fun onIncomeChanged(income: Float)

    abstract fun onExpensesChanged(expenses: ArrayList<Expense>)

    protected fun getExpenses() = expenseItemsLiveData?.value?.transformToArrayList() ?: arrayListOf()

    private fun List<Expense>.transformToArrayList() = arrayListOf(*(toTypedArray()))

    protected fun calculateTotal(expenses: ArrayList<Expense>) = expenses.sumByDouble {
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
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
    protected val databaseManager: DatabaseManager,
    protected val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var expensesLiveData: LiveData<ArrayList<Expense>>? = null

    private val expensesObserver = Observer<ArrayList<Expense>> {
        it?.let { onExpensesChanged(it) }
    }

    private val sharedPreferencesObserver = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == FloatKey.INCOME.getKey()) {
                onIncomeChanged(sharedPreferences.getFloat(FloatKey.INCOME))
            }
        }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesObserver)
    }

    fun observeExpenses(month: Int, year: Int) {
        logInfo("Observing expenses [month=$month, year=$year]")
        expensesLiveData?.removeObserver(expensesObserver)
        expensesLiveData = databaseManager.getExpensesOfMonth(year * 12 + month)
        expensesLiveData?.observeForever(expensesObserver)
    }

    abstract fun onIncomeChanged(income: Float)

    abstract fun onExpensesChanged(expenses: ArrayList<Expense>)

    @CallSuper
    override fun onCleared() {
        logInfo("View model cleared")
        expensesLiveData?.removeObserver(expensesObserver)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesObserver)
    }

}
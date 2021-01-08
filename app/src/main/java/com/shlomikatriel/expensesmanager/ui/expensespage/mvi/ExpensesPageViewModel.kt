package com.shlomikatriel.expensesmanager.ui.expensespage.mvi

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import javax.inject.Inject

class ExpensesPageViewModel(appContext: Context, val month: Int) : ViewModel() {

    @Inject
    lateinit var databaseManager: DatabaseManager

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewStateLiveData = MutableLiveData<ExpensesPageViewState>()

    private var viewState = ExpensesPageViewState()
        set(value) {
            field = value
            viewStateLiveData.value = value
        }

    private val expenseItemsLiveData: LiveData<List<Expense>>

    private val expenseItemsLiveDataObserver: Observer<List<Expense>> = Observer {
        logInfo("Expenses list changed")
        resultToViewState(ExpensesPageResult.ExpenseListChangedResult(it))
    }

    private val onSharedPreferencesChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _: SharedPreferences, key: String ->
            if (key == FloatKey.INCOME.getKey()) {
                val income = sharedPreferences.getFloat(FloatKey.INCOME)
                logInfo("Income shared preference changed")
                resultToViewState(ExpensesPageResult.IncomeChangedResult(income))
            }
        }

    init {
        (appContext as ExpensesManagerApp).appComponent.inject(this)
        expenseItemsLiveData = databaseManager.getExpensesOfMonth(month)
    }

    fun getViewState() = viewStateLiveData

    fun postEvent(expensesPageEvent: ExpensesPageEvent) {
        logInfo("Processing event $expensesPageEvent")
        when (expensesPageEvent) {
            is ExpensesPageEvent.InitializeEvent -> handleInitialize()
            is ExpensesPageEvent.SelectedChipsChangedEvent -> resultToViewState(
                ExpensesPageResult.SelectedChipsChangedResult(
                    expensesPageEvent.chips
                )
            )
        }
    }

    private fun handleInitialize() {
        expenseItemsLiveData.observeForever(expenseItemsLiveDataObserver)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferencesChangeListener)
    }

    private fun resultToViewState(result: ExpensesPageResult) {
        logDebug("Processing result $result")
        viewState = when (result) {
            is ExpensesPageResult.IncomeChangedResult -> {
                var balance = sharedPreferences.getFloat(FloatKey.INCOME)
                viewState.expenses.forEach { balance -= getCost(it) }
                viewState.copy(
                    balance = balance
                )
            }
            is ExpensesPageResult.ExpenseListChangedResult -> {
                var balance = sharedPreferences.getFloat(FloatKey.INCOME)
                result.expenses.forEach { balance -= getCost(it) }
                viewState.copy(
                    balance = balance,
                    expenses = transformToArrayList(result.expenses)
                )
            }
            is ExpensesPageResult.SelectedChipsChangedResult -> {
                viewState.copy(selectedChips = result.chips)
            }
        }

    }

    private fun getCost(expense: Expense) = if (expense is Expense.Payments) {
        expense.cost / expense.payments
    } else {
        expense.cost
    }

    private fun transformToArrayList(expenses: List<Expense>) =
        arrayListOf(*(expenses.toTypedArray()))
            .apply { sortBy { it.timeStamp } }

    override fun onCleared() {
        super.onCleared()
        logInfo("Expenses page fragment view model cleared")
        expenseItemsLiveData.removeObserver(expenseItemsLiveDataObserver)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(
            onSharedPreferencesChangeListener
        )
    }
}

class ExpensesPageViewModelFactory(private val appContext: Context, private val month: Int) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ExpensesPageViewModel(appContext, month) as T
    }

}
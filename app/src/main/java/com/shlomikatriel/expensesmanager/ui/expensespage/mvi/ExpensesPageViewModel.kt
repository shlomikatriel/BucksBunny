package com.shlomikatriel.expensesmanager.ui.expensespage.mvi

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.ExpenseDao
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import javax.inject.Inject

class ExpensesPageViewModel(appContext: Context, val month: Int, val year: Int): ViewModel() {

    @Inject
    lateinit var expenseDao: ExpenseDao

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
        Logger.i("Expenses list changed")
        resultToViewState(ExpensesPageResult.ExpenseListChangedResult(it))
    }

    private val onSharedPreferencesChangeListener : SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _: SharedPreferences, key: String ->
        if (key == FloatKey.INCOME.getKey()) {
            val income = sharedPreferences.getFloat(FloatKey.INCOME)
            Logger.i("Income shared preference changed")
            resultToViewState(ExpensesPageResult.IncomeChangedResult(income))
        }
    }

    init {
        (appContext as ExpensesManagerApp).appComponent.inject(this)
        expenseItemsLiveData = expenseDao.getExpensesOfMonth(month, year)
    }

    fun getViewState() = viewStateLiveData

    fun postEvent(expensesPageEvent: ExpensesPageEvent) {
        Logger.i("Processing event $expensesPageEvent")
        when (expensesPageEvent) {
            is ExpensesPageEvent.InitializeEvent -> handleInitialize()
            is ExpensesPageEvent.SelectedChipsChangedEvent -> resultToViewState(ExpensesPageResult.SelectedChipsChangedResult(expensesPageEvent.chips))
        }
    }

    private fun handleInitialize() {
        expenseItemsLiveData.observeForever(expenseItemsLiveDataObserver)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferencesChangeListener)
    }

    private fun resultToViewState(result: ExpensesPageResult) {
        Logger.d("Processing result $result")
        viewState = when (result) {
            is ExpensesPageResult.IncomeChangedResult -> {
                var balance = sharedPreferences.getFloat(FloatKey.INCOME)
                viewState.expenses.forEach { balance -= it.amount }
                viewState.copy(
                    balance = balance
                )
            }
            is ExpensesPageResult.ExpenseListChangedResult -> {
                var balance = sharedPreferences.getFloat(FloatKey.INCOME)
                result.expenses.forEach { balance -= it.amount }
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

    private fun transformToArrayList(expenses: List<Expense>) = arrayListOf(*(expenses.toTypedArray()))
        .apply { sortBy { it.timeStamp } }

    override fun onCleared() {
        super.onCleared()
        Logger.i("Expenses page fragment view model cleared")
        expenseItemsLiveData.removeObserver(expenseItemsLiveDataObserver)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferencesChangeListener)
    }
}

class ExpensesPageViewModelFactory(private val appContext: Context, private val month: Int, private val year: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ExpensesPageViewModel(appContext, month, year) as T
    }

}
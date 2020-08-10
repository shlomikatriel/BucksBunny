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

    private val expenseItemsLiveDataObserver: Observer<List<Expense>> = Observer { items ->
        Logger.i("Expenses list changed")
        val expenses = items.filter { !it.isMonthly }
        val monthlyExpenses = items.filter { it.isMonthly }
        resultToViewState(ExpensesPageResult.ExpenseListChangedResult(expenses, monthlyExpenses))
    }

    private val onSharedPreferencesChangeListener : SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _: SharedPreferences, key: String ->
        if (key == FloatKey.INCOME.getKey()) {
            val income = sharedPreferences.getFloat(FloatKey.INCOME)
            Logger.i("Income shared preference changed [income=$income]")
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
            is ExpensesPageEvent.AddExpenseEvent -> handleAddExpense(expensesPageEvent.isMonthly, expensesPageEvent.offset)
        }
    }

    private fun handleInitialize() {
        Logger.d("Handling initialize event")
        expenseItemsLiveData.observeForever(expenseItemsLiveDataObserver)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferencesChangeListener)
    }

    private fun handleAddExpense(isMonthly: Boolean, offset: Int) {
        Logger.d("Handling add expense event [isMonthly=$isMonthly, offset=$offset")
    }

    private fun resultToViewState(result: ExpensesPageResult) {
        Logger.i("Processing result $result")
        viewState = when (result) {
            is ExpensesPageResult.IncomeChangedResult -> {
                var balance = sharedPreferences.getFloat(FloatKey.INCOME)
                viewState.expenses?.forEach { balance -= it.amount }
                viewState.monthlyExpenses?.forEach { balance -= it.amount }
                viewState.copy(
                    balance = balance
                )
            }
            is ExpensesPageResult.ExpenseListChangedResult -> {
                var balance = sharedPreferences.getFloat(FloatKey.INCOME)
                result.expenses.forEach { balance -= it.amount }
                result.monthlyExpenses.forEach { balance -= it.amount }
                viewState.copy(
                    balance = balance,
                    expenses = transformExpensesToRecyclerItems(result.expenses),
                    monthlyExpenses = transformExpensesToRecyclerItems(result.monthlyExpenses)
                )
            }
        }

    }

    private fun transformExpensesToRecyclerItems(expenses: List<Expense>) = expenses.filter { it.id != null }
        .map { ExpenseRecyclerItem(it.id!!, it.timeStamp, it.name, it.amount) }
        .let { arrayListOf(*(it.toTypedArray())) }
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
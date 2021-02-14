package com.shlomikatriel.expensesmanager.expenses.mvi

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.shlomikatriel.expensesmanager.BucksBunnyApp
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo

class ExpensesPageViewModel(application: Application) : ExpensesBaseViewModel(application) {

    private var selectedExpenseTypes: Set<ExpenseType> = hashSetOf()

    private val viewStateLiveData = MutableLiveData<ExpensesPageViewState>()

    private var viewState = ExpensesPageViewState()
        set(value) {
            field = value
            viewStateLiveData.value = value
        }

    fun getViewState() = viewStateLiveData

    init {
        (application as BucksBunnyApp).appComponent.inject(this)
    }

    override fun onIncomeChanged(income: Float) {
        // Not needed in pages
    }

    override fun onExpensesChanged(expenses: ArrayList<Expense>) {
        val filteredExpenses =
            filterExpensesUsingSelectedExpenseTypes(expenses, selectedExpenseTypes).sorted()
        resultToViewState(ExpensesPageResult(filteredExpenses))
    }

    fun postEvent(expensesPageEvent: ExpensesPageEvent) {
        logInfo("Processing event $expensesPageEvent")
        when (expensesPageEvent) {
            is ExpensesPageEvent.Initialize -> observeChanges(expensesPageEvent.month)
            is ExpensesPageEvent.SelectedExpenseTypesChange -> handleExpenseTypesChanged(
                expensesPageEvent.expenseTypes
            )
        }
    }

    private fun handleExpenseTypesChanged(selectedExpenseTypes: Set<ExpenseType>) {
        this.selectedExpenseTypes = selectedExpenseTypes
        val expenses = getExpenses()
        val filteredExpenses =
            filterExpensesUsingSelectedExpenseTypes(expenses, selectedExpenseTypes).sorted()
        resultToViewState(ExpensesPageResult(filteredExpenses))
    }

    private fun resultToViewState(result: ExpensesPageResult) {
        logDebug("Processing result $result")
        viewState = viewState.copy(
            expenses = result.expenses,
            total = calculateTotal(result.expenses).toFloat()
        )
    }

    private fun filterExpensesUsingSelectedExpenseTypes(
        expenses: ArrayList<Expense>,
        selectedExpenseTypes: Set<ExpenseType>
    ): ArrayList<Expense> {
        if (selectedExpenseTypes.isEmpty()) {
            return expenses
        }
        val newExpenses = expenses.filter { selectedExpenseTypes.contains(it.getExpenseType()) }
            .toTypedArray()
        return arrayListOf(*newExpenses)
    }

    private fun ArrayList<Expense>.sorted(): ArrayList<Expense> {
        sortBy { it.timeStamp }
        return this
    }

    override fun onCleared() {
        super.onCleared()
        logInfo("Expenses page fragment view model cleared")
    }
}
package com.shlomikatriel.expensesmanager.expenses.mvi

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo

class ExpensesPageViewModel(application: Application) : ExpensesBaseViewModel(application) {

    private var selectedChips: Set<Chip> = hashSetOf()

    private val viewStateLiveData = MutableLiveData<ExpensesPageViewState>()

    private var viewState = ExpensesPageViewState()
        set(value) {
            field = value
            viewStateLiveData.value = value
        }

    fun getViewState() = viewStateLiveData

    init {
        (application as ExpensesManagerApp).appComponent.inject(this)
    }

    override fun onIncomeChanged(income: Float) {
        // Not needed in pages
    }

    override fun onExpensesChanged(expenses: ArrayList<Expense>) {
        val filteredExpenses = filterExpensesUsingChips(expenses, selectedChips).sorted()
        resultToViewState(ExpensesPageResult(filteredExpenses))
    }

    fun postEvent(expensesPageEvent: ExpensesPageEvent) {
        logInfo("Processing event $expensesPageEvent")
        when (expensesPageEvent) {
            is ExpensesPageEvent.Initialize -> observeChanges(expensesPageEvent.month)
            is ExpensesPageEvent.SelectedChipsChange -> handleChipsChanged(expensesPageEvent.chips)
        }
    }

    private fun handleChipsChanged(selectedChips: Set<Chip>) {
        this.selectedChips = selectedChips
        val expenses = getExpenses()
        val filteredExpenses = filterExpensesUsingChips(expenses, selectedChips).sorted()
        resultToViewState(ExpensesPageResult(filteredExpenses))
    }

    private fun resultToViewState(result: ExpensesPageResult) {
        logDebug("Processing result $result")
        viewState = viewState.copy(
                expenses = result.expenses,
                total = calculateTotal(result.expenses).toFloat()
        )
    }

    private fun filterExpensesUsingChips(
            expenses: ArrayList<Expense>,
            selectedChips: Set<Chip>
    ): ArrayList<Expense> {
        val newExpenses = expenses.filter { Chip.shouldShow(it, selectedChips) }.toTypedArray()
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
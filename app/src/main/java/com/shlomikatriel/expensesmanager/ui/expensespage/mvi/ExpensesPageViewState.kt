package com.shlomikatriel.expensesmanager.ui.expensespage.mvi

import com.shlomikatriel.expensesmanager.database.Expense

data class ExpensesPageViewState(
    val balance: Float? = null,
    val expenses: ArrayList<ExpenseRecyclerItem>? = null,
    val monthlyExpenses: ArrayList<ExpenseRecyclerItem>? = null
)

sealed class ExpensesPageEvent {
    object InitializeEvent : ExpensesPageEvent()
    data class AddExpenseEvent(val isMonthly: Boolean, val offset: Int) : ExpensesPageEvent()
}

sealed class ExpensesPageResult {
    data class IncomeChangedResult(val income: Float) : ExpensesPageResult()
    data class ExpenseListChangedResult(val expenses: List<Expense>, val monthlyExpenses: List<Expense>) : ExpensesPageResult()
}

data class ExpenseRecyclerItem(val id: Long, val timeStamp: Long, val name: String, val amount: Float)
package com.shlomikatriel.expensesmanager.expenses.mvi

import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType

data class ExpensesPageViewState(
    val expenses: ArrayList<Expense> = arrayListOf(),
    val total: Float? = null
)

sealed class ExpensesPageEvent {
    data class Initialize(val month: Int) : ExpensesPageEvent()
    data class SelectedExpenseTypesChange(val expenseTypes: Set<ExpenseType>) : ExpensesPageEvent()
}

data class ExpensesPageResult(val expenses: ArrayList<Expense>)
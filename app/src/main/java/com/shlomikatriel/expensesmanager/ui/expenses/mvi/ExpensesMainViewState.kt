package com.shlomikatriel.expensesmanager.ui.expenses.mvi

data class ExpensesMainViewState(
    val time: Long? = null,
    val forceSelectPage: Int? = null,
    val income: Float? = null,
    val expenses: Float? = null
)

sealed class ExpensesMainEvent {
    object Initialize : ExpensesMainEvent()
    data class MonthChange(val newPosition: Int): ExpensesMainEvent()
}

sealed class ExpensesMainResult {
    data class Initialize(val position: Int, val income: Float, val expenses: Float) : ExpensesMainResult()
    data class MonthChange(val newPosition: Int) : ExpensesMainResult()
    data class IncomeChange(val income: Float) : ExpensesMainResult()
    data class ExpensesChange(val expenses: Float) : ExpensesMainResult()
}
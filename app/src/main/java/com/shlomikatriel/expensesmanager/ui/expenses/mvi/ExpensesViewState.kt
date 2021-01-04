package com.shlomikatriel.expensesmanager.ui.expenses.mvi

data class ExpensesViewState(
    val time: Long? = null,
    val forceSelectPage: Int? = null
)

sealed class ExpensesEvent {
    object InitializeEvent : ExpensesEvent()
    data class MonthChangeEvent(val newPosition: Int): ExpensesEvent()
}

sealed class ExpensesResult {
    object InitializeResult : ExpensesResult()
    data class MonthChangeResult(val newPosition: Int) : ExpensesResult()
}
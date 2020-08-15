package com.shlomikatriel.expensesmanager.ui.expensespage.mvi

import androidx.annotation.Keep
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.database.Expense

data class ExpensesPageViewState(
    val balance: Float? = null,
    val expenses: ArrayList<Expense> = arrayListOf(),
    val selectedChips: List<Chip> = Chip.values().toList()
)

sealed class ExpensesPageEvent {
    object InitializeEvent : ExpensesPageEvent()
    data class SelectedChipsChangedEvent(val chips: List<Chip>) : ExpensesPageEvent()
}

sealed class ExpensesPageResult {
    data class IncomeChangedResult(val income: Float) : ExpensesPageResult()
    data class ExpenseListChangedResult(
        val expenses: List<Expense>
    ) : ExpensesPageResult()

    data class SelectedChipsChangedResult(
        val chips: List<Chip>
    ) : ExpensesPageResult()
}

@Keep
enum class Chip(private val predicate: (Expense) -> Boolean) {
    ONE_TIME({ !it.isMonthly }),
    MONTHLY({ it.isMonthly }),
    X_OR_MORE({ it.amount >= BuildConfig.CHIP_FILTER_THRESHOLD }),
    LESS_THEN_X({ it.amount < BuildConfig.CHIP_FILTER_THRESHOLD });

    companion object {
        fun shouldShow(
            expense: Expense,
            selectedChips: List<Chip>
        ): Boolean {
            val compliesToExpenseFrequency = selectedChips.intersect(listOf(MONTHLY, ONE_TIME))
                .any { it.predicate(expense) }
            val complyToCostRange = selectedChips.intersect(listOf(X_OR_MORE, LESS_THEN_X))
                .any { it.predicate(expense) }
            return compliesToExpenseFrequency && complyToCostRange
        }
    }
}
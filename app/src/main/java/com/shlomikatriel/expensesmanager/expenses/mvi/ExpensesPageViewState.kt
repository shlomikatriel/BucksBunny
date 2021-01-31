package com.shlomikatriel.expensesmanager.expenses.mvi

import androidx.annotation.Keep
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.database.Expense

data class ExpensesPageViewState(
    val expenses: ArrayList<Expense> = arrayListOf(),
    val total: Float? = null
)

sealed class ExpensesPageEvent {
    data class Initialize(val month: Int) : ExpensesPageEvent()
    data class SelectedChipsChange(val chips: Set<Chip>) : ExpensesPageEvent()
}

data class ExpensesPageResult(val expenses: ArrayList<Expense>)

@Keep
enum class Chip(private val predicate: (Expense) -> Boolean) {
    ONE_TIME({ it is Expense.OneTime }),
    MONTHLY({ it is Expense.Monthly }),
    PAYMENTS({ it is Expense.Payments }),
    X_OR_MORE({ it.cost >= BuildConfig.CHIP_FILTER_THRESHOLD }),
    LESS_THEN_X({ it.cost < BuildConfig.CHIP_FILTER_THRESHOLD });

    companion object {
        fun shouldShow(
            expense: Expense,
            selectedChips: Set<Chip>
        ): Boolean {
            val compliesWithFrequencyChips = doesComplyWithChipGroup(
                expense,
                selectedChips,
                setOf(ONE_TIME, MONTHLY, PAYMENTS)
            )
            val compliesWithCostRangeChips = doesComplyWithChipGroup(
                expense,
                selectedChips,
                setOf(X_OR_MORE, LESS_THEN_X)
            )
            return compliesWithFrequencyChips && compliesWithCostRangeChips
        }

        /**
         * In case no chip selected from group we act as if all are selected.
         * This is required for the initial state in which chips are not selected.
         * */
        private fun doesComplyWithChipGroup(
            expense: Expense,
            selectedChips: Set<Chip>,
            chipGroup: Set<Chip>
        ): Boolean {
            val selectedGroupChips = selectedChips.intersect(chipGroup)
            return selectedGroupChips.isEmpty() || selectedGroupChips.any { it.predicate(expense) }
        }
    }
}
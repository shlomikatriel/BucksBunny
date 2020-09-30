package com.shlomikatriel.expensesmanager.ui.expensespage.mvi

import androidx.annotation.Keep
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.database.Expense

data class ExpensesPageViewState(
    val balance: Float? = null,
    val expenses: ArrayList<Expense> = arrayListOf(),
    val selectedChips: Set<Chip> = Chip.values().toSet()
)

sealed class ExpensesPageEvent {
    object InitializeEvent : ExpensesPageEvent()
    data class SelectedChipsChangedEvent(val chips: Set<Chip>) : ExpensesPageEvent()
}

sealed class ExpensesPageResult {
    data class IncomeChangedResult(val income: Float) : ExpensesPageResult()
    data class ExpenseListChangedResult(
        val expenses: List<Expense>
    ) : ExpensesPageResult()

    data class SelectedChipsChangedResult(
        val chips: Set<Chip>
    ) : ExpensesPageResult()
}

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
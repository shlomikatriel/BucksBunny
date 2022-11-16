package com.shlomikatriel.expensesmanager.database.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.shlomikatriel.expensesmanager.R

@Keep
enum class ExpenseType(
    @StringRes val displayText: Int,
    @StringRes val filterContentDescription: Int
) {
    ONE_TIME(
        displayText = R.string.expenses_page_chip_one_time,
        filterContentDescription = R.string.filter_one_time_expenses
    ),
    MONTHLY(
        displayText = R.string.expenses_page_chip_monthly,
        filterContentDescription = R.string.filter_monthly_expenses
    ),
    PAYMENTS(
        displayText = R.string.expenses_page_chip_payments,
        filterContentDescription = R.string.filter_payments_expenses
    )
}
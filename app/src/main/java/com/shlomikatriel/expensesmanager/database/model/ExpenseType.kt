package com.shlomikatriel.expensesmanager.database.model

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.shlomikatriel.expensesmanager.R

@Keep
enum class ExpenseType(
    @StringRes val displayText: Int
) {
    ONE_TIME(
        displayText = R.string.expenses_page_chip_one_time
    ),
    MONTHLY(
        displayText = R.string.expenses_page_chip_monthly
    ),
    PAYMENTS(
        displayText = R.string.expenses_page_chip_payments
    )
}
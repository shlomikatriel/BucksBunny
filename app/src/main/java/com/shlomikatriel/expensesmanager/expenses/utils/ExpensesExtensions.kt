package com.shlomikatriel.expensesmanager.expenses.utils

import androidx.annotation.StringRes
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType

fun ArrayList<Expense>.filteredAndSorted(types: Set<ExpenseType>, text: String): ArrayList<Expense> {
    val filtered = filter {
        types.isEmpty() || types.contains(it.getExpenseType())
    }.filter {
        it.name.contains(text)
    }
    return ArrayList(filtered).apply {
        sortBy { it.timeStamp }
    }
}

fun ArrayList<Expense>.calculateTotal() = sumOf {
    when (it) {
        is Expense.OneTime, is Expense.Monthly -> it.cost.toDouble()
        is Expense.Payments -> (it.cost / it.payments).toDouble()
    }
}.toFloat()

@StringRes
fun ExpenseType.getAddButtonText() = when (this) {
    ExpenseType.ONE_TIME -> R.string.add_one_time_button_title
    ExpenseType.MONTHLY -> R.string.add_monthly_button_title
    ExpenseType.PAYMENTS -> R.string.add_payments_button_title
}

@StringRes
fun ExpenseType.getAddDialogTitle() = when (this) {
    ExpenseType.ONE_TIME -> R.string.add_one_time_expense_dialog_title
    ExpenseType.MONTHLY -> R.string.add_monthly_expense_dialog_title
    ExpenseType.PAYMENTS -> R.string.add_payments_expense_dialog_title
}

@StringRes
fun ExpenseType.getUpdateDialogTitle() = when (this) {
    ExpenseType.ONE_TIME -> R.string.update_one_time_expense_dialog_title
    ExpenseType.MONTHLY -> R.string.update_monthly_expense_dialog_title
    ExpenseType.PAYMENTS -> R.string.update_payments_expense_dialog_title
}
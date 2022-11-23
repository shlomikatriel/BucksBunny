package com.shlomikatriel.expensesmanager.expenses.utils

import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.logVerbose

object ExpensesUtils {
    fun isInputValid(expenseType: ExpenseType, name: String?, cost: Float?, payments: Int?): Boolean {
        val nameValid = !name.isNullOrEmpty()
        val costValid = cost != null && cost >= 0f
        val paymentsValid = payments != null && payments > 0
        return if (expenseType == ExpenseType.PAYMENTS) {
            nameValid && costValid && paymentsValid
        } else {
            nameValid && costValid
        }
    }

    fun create(expenseType: ExpenseType, name: String, cost: Float, payments: Int?, month: Int, year: Int): Expense {
        logVerbose(Tag.EXPENSES, "Creating expense [expenseType=$expenseType, name=$name, cost=$cost, payments=$payments, month=$month, year=$year]")
        return when (expenseType) {
            ExpenseType.ONE_TIME -> Expense.OneTime(
                databaseId = null,
                timeStamp = System.currentTimeMillis(),
                name = name,
                cost = cost,
                month = year * 12 + month
            )
            ExpenseType.MONTHLY -> Expense.Monthly(
                databaseId = null,
                timeStamp = System.currentTimeMillis(),
                name = name,
                cost = cost
            )
            ExpenseType.PAYMENTS -> Expense.Payments(
                databaseId = null,
                timeStamp = System.currentTimeMillis(),
                name = name,
                cost = cost,
                month = year * 12 + month,
                payments = payments!!
            )
        }
    }
}
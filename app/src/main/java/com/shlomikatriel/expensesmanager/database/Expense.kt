package com.shlomikatriel.expensesmanager.database

import com.shlomikatriel.expensesmanager.database.model.*

sealed class Expense {
    abstract val databaseId: Long?
    abstract val timeStamp: Long
    abstract val name: String
    abstract val cost: Float

    data class OneTime(
        override val databaseId: Long?,
        override val timeStamp: Long,
        override val name: String,
        override val cost: Float,
        val month: Int // This value is the months that passed since year 0
    ) : Expense() {
        fun toModel() = OneTimeExpenseModel(databaseId, toDetails(), month)
    }

    data class Monthly(
        override val databaseId: Long?,
        override val timeStamp: Long,
        override val name: String,
        override val cost: Float
    ) : Expense() {
        fun toModel() = MonthlyExpenseModel(databaseId, toDetails())
    }

    data class Payments(
        override val databaseId: Long?,
        override val timeStamp: Long,
        override val name: String,
        override val cost: Float,
        val month: Int, // This value is the months that passed since year 0
        val payments: Int
    ) : Expense() {
        fun toModel() = PaymentsExpenseModel(databaseId, toDetails(), month, payments)
    }

    fun toDetails() = ExpenseDetails(timeStamp, name, cost)

    fun getExpenseType() = when (this) {
        is OneTime -> ExpenseType.ONE_TIME
        is Monthly -> ExpenseType.MONTHLY
        is Payments -> ExpenseType.PAYMENTS
    }
}
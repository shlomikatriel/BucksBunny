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
        fun copyPayments(payments: Int) = copy(payments = payments)
    }

    fun toDetails() = ExpenseDetails(timeStamp, name, cost)

    fun getExpenseType() = when (this) {
        is OneTime -> ExpenseType.ONE_TIME
        is Monthly -> ExpenseType.MONTHLY
        is Payments -> ExpenseType.PAYMENTS
    }

    fun copyName(name: String) = when (this) {
        is OneTime -> copy(name = name)
        is Monthly -> copy(name = name)
        is Payments -> copy(name = name)
    }

    fun copyCost(cost: Float) = when (this) {
        is OneTime -> copy(cost = cost)
        is Monthly -> copy(cost = cost)
        is Payments -> copy(cost = cost)
    }
}
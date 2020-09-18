package com.shlomikatriel.expensesmanager.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shlomikatriel.expensesmanager.database.Expense

@Keep
@Entity(tableName = "one_time_expense")
data class OneTimeExpenseModel(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @Embedded val details: ExpenseDetails,
    val month: Int // This value is the months that passed since year 0
) {
    fun toExpense() = Expense.OneTime(id!!, details.timeStamp, details.name, details.cost, month)
}

@Keep
@Entity(tableName = "monthly_expense")
data class MonthlyExpenseModel(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @Embedded val details: ExpenseDetails
) {
    fun toExpense() = Expense.Monthly(id!!, details.timeStamp, details.name, details.cost)
}

@Keep
@Entity(tableName = "payments_expense")
data class PaymentsExpenseModel(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @Embedded val details: ExpenseDetails,
    val month: Int, // This value is the months that passed since year 0
    val payments: Int
) {
    fun toExpense() =
        Expense.Payments(id!!, details.timeStamp, details.name, details.cost, month, payments)
}

@Keep
data class ExpenseDetails(
    @ColumnInfo(name = "time_stamp") val timeStamp: Long,
    val name: String,
    val cost: Float
)
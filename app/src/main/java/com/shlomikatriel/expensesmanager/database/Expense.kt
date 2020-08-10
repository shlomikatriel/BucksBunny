package com.shlomikatriel.expensesmanager.database

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "time_stamp") val timeStamp: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amount") val amount: Float,
    @ColumnInfo(name = "is_monthly") val isMonthly: Boolean,
    @ColumnInfo(name = "year") val year: Int? = null,
    @ColumnInfo(name = "month") val month: Int? = null
)
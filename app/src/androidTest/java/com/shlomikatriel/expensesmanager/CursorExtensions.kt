package com.shlomikatriel.expensesmanager

import android.database.Cursor
import androidx.core.database.getStringOrNull
import org.junit.Assert

fun Cursor.assertRowCount(expected: Int) {
    Assert.assertEquals(
        "Incorrect row count",
        expected,
        count
    )
}

fun Cursor.assertColumnCount(expected: Int) {
    Assert.assertEquals(
        "Incorrect column count",
        expected,
        columnCount
    )
}

fun Cursor.assertColumnValue(columnName: String, expectedValue: Int) {
    Assert.assertEquals(
        "Incorrect int value of column $columnName",
        expectedValue.toLong(),
        getInt(getColumnIndex(columnName)).toLong()
    )
}

fun Cursor.assertColumnValue(columnName: String, expectedValue: Float) {
    Assert.assertEquals(
        "Incorrect float value of column $columnName",
        expectedValue,
        getFloat(getColumnIndex(columnName)),
        0.1f
    )
}

fun Cursor.assertColumnValue(columnName: String, expectedValue: String) {
    Assert.assertEquals(
        "Incorrect string value of column $columnName",
        expectedValue,
        getStringOrNull(getColumnIndex(columnName))
    )
}
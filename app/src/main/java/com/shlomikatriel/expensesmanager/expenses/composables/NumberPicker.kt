package com.shlomikatriel.expensesmanager.expenses.composables

import android.content.Context
import android.os.Build
import android.util.TypedValue
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.NumberPicker as AndroidNumberPicker


@Composable
fun NumberPicker(
    minValue: Int,
    maxValue: Int,
    initialValue: Int,
    displayValues: Array<String>? = null,
    onValueChange: (value: Int) -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }
    AndroidView(
        factory = {
            AndroidNumberPicker(it).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    this.textSize = getTextSize(it)
                }
                this.minValue = minValue
                this.maxValue = maxValue
                this.value = value
                if (displayValues != null) {
                    displayedValues = displayValues
                }
                setOnValueChangedListener { _, _, newValue ->
                    value = newValue
                    onValueChange(newValue)
                }
            }
        }
    )
}

private fun getTextSize(context: Context): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, context.resources.displayMetrics)
}
package com.shlomikatriel.expensesmanager

import android.content.Context
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.shlomikatriel.expensesmanager.databinding.IncomeInputLayoutBinding
import com.shlomikatriel.expensesmanager.logs.logError

fun Fragment.configureToolbar() {
    if (activity is MainActivity) {
        (activity as MainActivity).showToolbar()
    } else {
        logError("Can't configure toolbar, main activity is null for fragment '${javaClass.simpleName}'")
    }
}

fun Fragment.hideToolbar() {
    if (activity is MainActivity) {
        (activity as MainActivity).hideToolbar()
    } else {
        logError("Can't hide toolbar, main activity is null for fragment '${javaClass.simpleName}'")
    }
}

fun IncomeInputLayoutBinding.isInputValid(
    context: Context
): Boolean {
    val income = income.text.toString()
    val incomeBlank = income.isBlank()
    val incomeAsFloat = income.toFloatOrNull()

    when {
        incomeBlank -> incomeLayout.showError(context, R.string.error_empty_value)
        incomeAsFloat == null -> incomeLayout.showError(context, R.string.error_number_illegal)
    }

    return !incomeBlank && incomeAsFloat != null
}

fun IncomeInputLayoutBinding.initialize(currencySymbol: String, currentIncome: Float) {
    incomeLayout.prefixText = currencySymbol
    income.setText(
        currentIncome.toString(),
        TextView.BufferType.NORMAL
    )
}


fun TextInputLayout.showError(context: Context, @StringRes errorRes: Int) {
    error = context.getString(errorRes)
    postDelayed({
        error = null
    }, 1500L)
}
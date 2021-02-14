package com.shlomikatriel.expensesmanager

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.databinding.DialogExpenseInputsBinding
import com.shlomikatriel.expensesmanager.databinding.IncomeInputLayoutBinding
import com.shlomikatriel.expensesmanager.logs.logError

fun Fragment.configureToolbar(@StringRes title: Int, navigateUpEnabled: Boolean = false) {
    if (activity is MainActivity) {
        setHasOptionsMenu(true)
        (activity as MainActivity).configureToolbar(title, navigateUpEnabled)
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

fun DialogExpenseInputsBinding.initialize(currencySymbol: String, type: ExpenseType) {
    costLayout.prefixText = currencySymbol
    if (type == ExpenseType.PAYMENTS) {
        paymentsLayout.visibility = View.VISIBLE
    }
}

/**
 * @return if all of the fields are valid
 * */
fun DialogExpenseInputsBinding.isInputValid(
    type: ExpenseType,
    context: Context
): Boolean {
    val name = name.text.toString()
    val costAsString = cost.text.toString()
    val cost = costAsString.toFloatOrNull()
    val paymentsAsString = payments.text.toString()
    val payments = paymentsAsString.toIntOrNull()
    val nameBlank = name.isBlank()
    val costBlank = costAsString.isBlank()
    val paymentsBlank = paymentsAsString.isBlank()

    if (nameBlank) {
        nameLayout.showError(context, R.string.error_empty_value)
    }

    when {
        costBlank -> costLayout.showError(context, R.string.error_empty_value)
        cost == null -> costLayout.showError(context, R.string.error_number_illegal)
    }

    if (type == ExpenseType.PAYMENTS) when {
        paymentsBlank -> paymentsLayout.showError(context, R.string.error_empty_value)
        payments == null -> paymentsLayout.showError(context, R.string.error_number_illegal)
    }

    return !nameBlank && !costBlank && cost != null && if (type == ExpenseType.PAYMENTS) {
        !paymentsBlank && payments != null
    } else {
        true
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
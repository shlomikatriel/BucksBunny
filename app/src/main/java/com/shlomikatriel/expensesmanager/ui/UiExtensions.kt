package com.shlomikatriel.expensesmanager.ui

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.databinding.DialogExpenseInputsBinding
import com.shlomikatriel.expensesmanager.logs.Logger

fun Fragment.configureToolbar(
    @StringRes title: Int,
    navigateUpEnabled: Boolean = false
) {
    setHasOptionsMenu(true)
    (activity as MainActivity?)?.configureToolbar(
        title,
        navigateUpEnabled
    ) ?: Logger.e("Main activity is null for fragment '${javaClass.simpleName}'")
}

fun View.startPopAnimation() {
    val animation = ScaleAnimation(
        1f,
        1.4f,
        1f,
        1.4f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    ).apply {
        duration = 150
        repeatCount = 1
        repeatMode = Animation.REVERSE
    }
    startAnimation(animation)
}

fun DialogExpenseInputsBinding.initialize(currencySymbol: String) {
    typeButtons.addOnButtonCheckedListener { _, checkedId, isChecked ->
        paymentsLayout.visibility = if (checkedId == R.id.payments_expense && isChecked) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
    costLayout.prefixText = currencySymbol
}

fun DialogExpenseInputsBinding.getSelectedExpenseType() = typeButtons.checkedButtonId.let {
    root.findViewById<View>(it).tag as ExpenseType
}

/**
 * @return if all of the fields are valid
 * */
fun DialogExpenseInputsBinding.isInputValid(
    context: Context
): Boolean {
    val type = getSelectedExpenseType()
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

fun TextInputLayout.showError(context: Context, @StringRes errorRes: Int) {
    error = context.getString(errorRes)
    postDelayed({
        error = null
    }, 1500L)
}
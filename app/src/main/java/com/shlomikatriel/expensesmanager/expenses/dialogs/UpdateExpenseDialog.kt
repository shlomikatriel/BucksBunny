package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shlomikatriel.expensesmanager.LocalizationManager
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.logs.logInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
private class UpdateExpenseViewModel
@Inject constructor(
    private val databaseManager: DatabaseManager,
    private val localizationManager: LocalizationManager
) : ViewModel() {

    private var initialized = false

    private lateinit var initialExpense: Expense
    private lateinit var expense: Expense

    fun initialize(expense: Expense) {
        if (!initialized) {
            logInfo("Initializing update expense view model")
            this.initialExpense = expense
            this.expense = expense
        }
    }

    fun getCurrencySymbol() = localizationManager.getCurrencySymbol()

    fun onNameChanged(name: String) {
        logInfo("Name changed: ${name.hashCode()}")
        expense = expense.copyName(name)
    }

    fun onCostChanged(cost: Float) {
        logInfo("Cost changed: ${cost.hashCode()}")
        expense = expense.copyCost(cost)
    }

    fun onPaymentsChanged(payments: Int) {
        logInfo("Payments changed: ${payments.hashCode()}")
        expense.let {
            if (it is Expense.Payments) {
                expense = it.copyPayments(payments)
            }
        }
    }

    fun onConfirmed() = viewModelScope.launch(context = Dispatchers.IO) {
        val changed = initialExpense != expense
        logInfo("Updating expense [changed=$changed]")
        if (changed) {
            databaseManager.update(expense)
        }
    }
}

@Composable
fun UpdateExpenseDialog(
    initialExpense: Expense,
    dismiss: () -> Unit
) {
    val model: UpdateExpenseViewModel = viewModel()
    model.initialize(initialExpense)
    UpdateExpenseContent(
        initialExpense = initialExpense,
        currencySymbol = model.getCurrencySymbol(),
        onNameChanged = model::onNameChanged,
        onCostChanged = model::onCostChanged,
        onPaymentsChanged = model::onPaymentsChanged,
        onConfirmed = model::onConfirmed,
        dismiss = dismiss
    )
}

@Composable
private fun UpdateExpenseContent(
    initialExpense: Expense,
    currencySymbol: String,
    onNameChanged: (name: String) -> Unit,
    onCostChanged: (cost: Float) -> Unit,
    onPaymentsChanged: (payments: Int) -> Unit,
    onConfirmed: () -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = dismiss,
        buttons = {
            // Force ltr to pin the OK button to the right
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(id = R.dimen.dialog_padding))
                ) {
                    OutlinedButton(
                        onClick = dismiss
                    ) {
                        AppText(R.string.dialog_cancel)
                    }

                    Button(
                        onClick = {
                            onConfirmed()
                            dismiss()
                        }
                    ) {
                        AppText(R.string.update)
                    }
                }
            }
        },
        modifier = modifier,
        title = {
            AppText(
                getDialogTitle(initialExpense),
                style = MaterialTheme.typography.h6,
                bold = true
            )
        },
        text = {
            ExpenseInput(
                currencySymbol = currencySymbol,
                showPayments = initialExpense is Expense.Payments,
                onNameChanged = {
                    onNameChanged(it)
                },
                onCostChanged = {
                    onCostChanged(it)
                },
                onPaymentsChanged = {
                    if (initialExpense is Expense.Payments) {
                        onPaymentsChanged(it)
                    }
                },
                initialName = initialExpense.name,
                initialCost = initialExpense.cost,
                initialPayments = if (initialExpense is Expense.Payments) {
                    initialExpense.payments
                } else {
                    null
                }
            )
        }
    )
}

@StringRes
private fun getDialogTitle(expense: Expense) = when (expense) {
    is Expense.Monthly -> R.string.update_monthly_expense_dialog_title
    is Expense.OneTime -> R.string.update_one_time_expense_dialog_title
    is Expense.Payments -> R.string.update_payments_expense_dialog_title
}
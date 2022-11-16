package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.expenses.utils.ExpensesUtils
import com.shlomikatriel.expensesmanager.expenses.utils.getAddDialogTitle

@Composable
fun AddExpenseDialog(
    expenseType: ExpenseType,
    onConfirm: (expenseType: ExpenseType, name: String, cost: Float, payments: Int?) -> Unit,
    onDismissRequest: () -> Unit
) {
    var name by remember { mutableStateOf(null as String?) }
    var cost by remember { mutableStateOf(null as Float?) }
    var payments by remember { mutableStateOf(null as Int?) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    if (ExpensesUtils.isInputValid(expenseType, name, cost, payments)) {
                        onDismissRequest()
                        onConfirm(expenseType, name!!, cost!!, payments)
                    }
                },
                enabled = ExpensesUtils.isInputValid(expenseType, name, cost, payments)
            ) {
                Text(stringResource(R.string.add_expense_dialog_add))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        title = {
            Text(
                stringResource(expenseType.getAddDialogTitle()),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            ExpenseInput(
                showPayments = expenseType == ExpenseType.PAYMENTS,
                onNameChanged = {
                    name = it
                },
                onCostChanged = {
                    cost = it
                },
                onPaymentsChanged = {
                    if (expenseType == ExpenseType.PAYMENTS) {
                        payments = it
                    }
                }
            )
        }
    )
}
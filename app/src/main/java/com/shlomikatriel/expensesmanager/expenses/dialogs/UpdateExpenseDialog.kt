package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.expenses.utils.ExpensesUtils
import com.shlomikatriel.expensesmanager.expenses.utils.getUpdateDialogTitle

@Composable
fun UpdateExpenseDialog(
    expense: Expense,
    onConfirm: (expense: Expense) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(expense.name as String?) }
    var cost by remember { mutableStateOf(expense.cost as Float?) }
    var payments by remember { mutableStateOf(if (expense is Expense.Payments) expense.payments else null) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    if (ExpensesUtils.isInputValid(expense.getExpenseType(), name, cost, payments)) {
                        onDismissRequest()
                        var updatedExpense = expense
                        name?.let {
                            updatedExpense = updatedExpense.copyName(it)
                        }
                        cost?.let {
                            updatedExpense = updatedExpense.copyCost(it)
                        }
                        if (updatedExpense is Expense.Payments) {
                            payments?.let {
                                updatedExpense = (updatedExpense as Expense.Payments).copyPayments(it)
                            }
                        }
                        onConfirm(updatedExpense)
                    }
                },
                enabled = ExpensesUtils.isInputValid(expense.getExpenseType(), name, cost, payments)
            ) {
                Text(stringResource(R.string.update))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        modifier = modifier,
        title = {
            Text(
                stringResource(expense.getExpenseType().getUpdateDialogTitle()),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            ExpenseInput(
                showPayments = expense is Expense.Payments,
                onNameChanged = {
                    name = it
                },
                onCostChanged = {
                    cost = it
                },
                onPaymentsChanged = {
                    if (expense is Expense.Payments) {
                        payments = it
                    }
                },
                initialName = expense.name,
                initialCost = expense.cost,
                initialPayments = if (expense is Expense.Payments) {
                    expense.payments
                } else {
                    null
                }
            )
        }
    )
}
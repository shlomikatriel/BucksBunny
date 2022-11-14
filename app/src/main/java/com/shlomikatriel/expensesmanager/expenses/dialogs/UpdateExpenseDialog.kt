package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
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
                        onClick = onDismissRequest
                    ) {
                        Text(stringResource(R.string.dialog_cancel))
                    }

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
                }
            }
        },
        modifier = modifier,
        title = {
            Text(
                stringResource(expense.getExpenseType().getUpdateDialogTitle()),
                style = MaterialTheme.typography.h6,
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
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
                            if (ExpensesUtils.isInputValid(expenseType, name, cost, payments)) {
                                onDismissRequest()
                                onConfirm(expenseType, name!!, cost!!, payments)
                            }
                        },
                        enabled = ExpensesUtils.isInputValid(expenseType, name, cost, payments)
                    ) {
                        Text(stringResource(R.string.add_expense_dialog_add))
                    }
                }
            }
        },
        title = {
            Text(
                stringResource(expenseType.getAddDialogTitle()),
                style = MaterialTheme.typography.h6,
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
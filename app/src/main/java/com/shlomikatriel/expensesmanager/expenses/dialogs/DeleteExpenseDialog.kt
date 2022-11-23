package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.Expense

@Composable
fun DeleteExpenseDialog(
    expense: Expense,
    onConfirm: (expense: Expense) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(expense)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        title = {
            Text(
                stringResource(R.string.delete_expense_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                stringResource(R.string.delete_expense_dialog_confirmation_text),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )
}
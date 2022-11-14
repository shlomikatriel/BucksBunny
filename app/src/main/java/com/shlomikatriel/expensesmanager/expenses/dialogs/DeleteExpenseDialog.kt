package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
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
                            onConfirm(expense)
                            onDismissRequest()
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        },
        title = {
            Text(
                stringResource(R.string.delete_expense_dialog_title),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                stringResource(R.string.delete_expense_dialog_confirmation_text),
                style = MaterialTheme.typography.body1
            )
        }
    )
}
package com.shlomikatriel.expensesmanager.preferences.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.preferences.components.IncomeInput

@Composable
fun ChooseIncomeDialog(
    initialIncome: Float,
    onConfirm: (income: Float) -> Unit,
    onDismissRequest: () -> Unit
) {
    var income by remember { mutableStateOf(initialIncome as Float?) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    income?.let {
                        onDismissRequest()
                        onConfirm(it)
                    }
                },
                enabled = income != null
            ) {
                Text(stringResource(R.string.choose_income_dialog_ok))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        title = null,
        text = {
            IncomeInput(false, initialIncome) {
                income = it
            }
        }
    )
}
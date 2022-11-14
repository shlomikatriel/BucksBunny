package com.shlomikatriel.expensesmanager.preferences.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
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
                            income?.let {
                                onDismissRequest()
                                onConfirm(it)
                            }
                        },
                        enabled = income != null
                    ) {
                        Text(stringResource(R.string.choose_income_dialog_ok))
                    }
                }
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
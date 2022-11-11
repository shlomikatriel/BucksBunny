package com.shlomikatriel.expensesmanager.preferences.components

import androidx.compose.runtime.*
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.preferences.components.base.Preference
import com.shlomikatriel.expensesmanager.preferences.dialogs.ChooseIncomeDialog
import java.text.NumberFormat

@Composable
fun IncomePreference(income: Float, onConfirm: (income: Float) -> Unit) {
    var dialogShown by remember { mutableStateOf(false) }
    Preference(R.string.preferences_monthly_income, subtitle = NumberFormat.getCurrencyInstance().format(income)) {
        dialogShown = true
    }
    if (dialogShown) {
        ChooseIncomeDialog(
            initialIncome = income,
            onConfirm = onConfirm,
            onDismissRequest = {
                dialogShown = false
            }
        )
    }
}
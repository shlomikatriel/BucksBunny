package com.shlomikatriel.expensesmanager.preferences.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppInfoText
import com.shlomikatriel.expensesmanager.compose.composables.AppTextField
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews
import java.util.*

@ComponentPreviews
@Composable
private fun IncomeInputPreview() = AppTheme {
    IncomeInput(true, 3004.4f) {}
}

@Composable
fun IncomeInput(
    isOnboarding: Boolean,
    initialIncome: Float,
    onIncomeChanged: (income: Float?) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dialog_vertical_spacing))
    ) {
        Text(
            text = stringResource(R.string.choose_income_dialog_title),
            style = MaterialTheme.typography.h5,
            color = if (isOnboarding) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.choose_income_dialog_description),
            style = MaterialTheme.typography.body1
        )

        if (isOnboarding) {
            AppInfoText(R.string.hint_income_can_be_changed_later)
        }

        var input by remember { mutableStateOf(initialIncome.toString()) }
        AppTextField(
            value = input,
            label = R.string.choose_income_dialog_hint,
            onValueChange = { value ->
                input = value
                onIncomeChanged(value.toFloatOrNull())
            },
            valueValidator = {
                val errorStringRes = validateInput(it)
                errorStringRes
            },
            trailingIcon = Currency.getInstance(Locale.getDefault()).symbol
        )
    }

}

@StringRes
private fun validateInput(income: String): Int? {
    val incomeBlank = income.isBlank()
    val incomeAsFloat = income.toFloatOrNull()

    return when {
        incomeBlank -> R.string.error_empty_value
        incomeAsFloat == null -> R.string.error_number_illegal
        else -> null
    }
}
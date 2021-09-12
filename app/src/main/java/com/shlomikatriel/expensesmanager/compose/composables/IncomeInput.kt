package com.shlomikatriel.expensesmanager.compose.composables

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme

@Preview(
    "Normal",
    showBackground = true,
    locale = "en"
)
@Preview(
    "Night",
    showBackground = true,
    locale = "iw",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun IncomeInputPreview() = AppTheme {
    IncomeInput(true, "₪", 3004.4f) {}
}

@Composable
fun IncomeInput(
    isOnboarding: Boolean,
    symbol: String,
    initialIncome: Float,
    onIncomeChanged: (income: Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dialog_vertical_spacing))
    ) {
        AppText(
            text = R.string.choose_income_dialog_title,
            style = MaterialTheme.typography.h5,
            color = if (isOnboarding) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
            bold = true
        )

        AppText(
            text = R.string.choose_income_dialog_description,
            style = MaterialTheme.typography.body1
        )

        if (isOnboarding) {
            AppInfoText(R.string.hint_income_can_be_changed_in_settings)
        }

        var input by remember { mutableStateOf(initialIncome.toString()) }
        AppTextField(
            value = input,
            label = R.string.choose_income_dialog_hint,
            onValueChange = { value ->
                input = value
                parseInput(value)?.let { income ->
                    onIncomeChanged(income)
                }
            },
            valueValidator = {
                val errorStringRes = validateInput(it)
                errorStringRes
            },
            trailingIcon = symbol
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

private fun parseInput(income: String) = income.toFloatOrNull()
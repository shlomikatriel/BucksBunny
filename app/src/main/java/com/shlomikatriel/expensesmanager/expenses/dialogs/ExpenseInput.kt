package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppTextField

@Preview(
    "Without Payments",
    showBackground = true
)
@Composable
private fun ExpenseInputPreviewWithoutPayments() = AppTheme {
    ExpenseInput(
        "$",
        false,
        {},
        {},
        {}
    )
}

@Preview(
    "With Payments",
    showBackground = true
)
@Composable
private fun ExpenseInputPreviewWithPayments() = AppTheme {
    ExpenseInput(
        "$",
        true,
        {},
        {},
        {},
        initialName = "Restaurant",
        initialCost = 100f,
        initialPayments = 3
    )
}

@Composable
fun ExpenseInput(
    currencySymbol: String,
    showPayments: Boolean,
    onNameChanged: (name: String) -> Unit,
    onCostChanged: (cost: Float) -> Unit,
    onPaymentsChanged: (payments: Int) -> Unit,
    modifier: Modifier = Modifier,
    initialName: String? = null,
    initialCost: Float? = null,
    initialPayments: Int? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dialog_vertical_spacing))
    ) {
        var name by remember { mutableStateOf(initialName ?: "") }
        AppTextField(
            value = name,
            label = R.string.choose_income_dialog_hint,
            onValueChange = { value ->
                name = value
                onNameChanged(value)
            },
            valueValidator = {
                if (it.isBlank()) {
                    R.string.error_empty_value
                } else {
                    null
                }
            }
        )
        var cost by remember { mutableStateOf(initialCost?.toString() ?: "") }
        AppTextField(
            value = cost,
            label = R.string.expense_cost,
            onValueChange = { value ->
                cost = value
                value.toFloatOrNull()?.let(onCostChanged)
            },
            valueValidator = {
                when {
                    it.isBlank() -> R.string.error_empty_value
                    it.toFloatOrNull() == null -> R.string.error_number_illegal
                    else -> null
                }
            },
            trailingIcon = currencySymbol
        )
        if (showPayments) {
            var payments by remember { mutableStateOf(initialPayments?.toString() ?: "") }
            AppTextField(
                value = payments,
                label = R.string.payments_count,
                onValueChange = { value ->
                    payments = value
                    value.toIntOrNull()?.let(onPaymentsChanged)
                },
                valueValidator = {
                    when {
                        it.isBlank() -> R.string.error_empty_value
                        it.toIntOrNull() == null -> R.string.error_number_illegal
                        else -> null
                    }
                }
            )
        }
    }
}
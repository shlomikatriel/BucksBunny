package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppTextField
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews
import java.util.*

@ComponentPreviews
@Composable
private fun ExpenseInputPreviewWithoutPayments() = AppTheme {
    ExpenseInput(
        false,
        {},
        {},
        {}
    )
}

@ComponentPreviews
@Composable
private fun ExpenseInputPreviewWithPayments() = AppTheme {
    ExpenseInput(
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
    showPayments: Boolean,
    onNameChanged: (name: String?) -> Unit,
    onCostChanged: (cost: Float?) -> Unit,
    onPaymentsChanged: (payments: Int?) -> Unit,
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
            label = R.string.expense_name,
            onValueChange = { value ->
                name = value
                onNameChanged(value)
            }
        )
        var cost by remember { mutableStateOf(initialCost?.toString() ?: "") }
        AppTextField(
            value = cost,
            label = R.string.expense_cost,
            onValueChange = { value ->
                cost = value
                onCostChanged(value.toFloatOrNull())
            },
            valueValidator = {
                when {
                    it.isBlank() -> null
                    it.toFloatOrNull() == null -> R.string.error_number_illegal
                    it.toFloat() <= 0 -> R.string.error_must_be_positive
                    else -> null
                }
            },
            keyboardType = KeyboardType.Number,
            trailingIcon = Currency.getInstance(Locale.getDefault()).symbol
        )
        if (showPayments) {
            var payments by remember { mutableStateOf(initialPayments?.toString() ?: "") }
            AppTextField(
                value = payments,
                label = R.string.payments_count,
                onValueChange = { value ->
                    payments = value
                    onPaymentsChanged(value.toIntOrNull())
                },
                valueValidator = {
                    when {
                        it.isBlank() -> null
                        it.toIntOrNull() == null -> R.string.error_number_illegal
                        it.toInt() <= 0 -> R.string.error_must_be_positive
                        else -> null
                    }
                },
                keyboardType = KeyboardType.Decimal,
            )
        }
    }
}
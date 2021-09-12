package com.shlomikatriel.expensesmanager.expenses.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.logs.logInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
private class DeleteExpenseViewModel
@Inject constructor(
    private val databaseManager: DatabaseManager
) : ViewModel() {

    private var initialized = false

    private lateinit var expense: Expense

    fun initialize(expense: Expense) {
        if (!initialized) {
            logInfo("Initializing delete expense view model")
            this.expense = expense
        }
    }

    fun onConfirmed() = viewModelScope.launch(context = Dispatchers.IO) {
        logInfo("Deleting expense")
        databaseManager.delete(expense)
    }
}

@Composable
fun DeleteExpenseDialog(
    expense: Expense,
    dismiss: () -> Unit
) {
    val model: DeleteExpenseViewModel = viewModel()
    model.initialize(expense)
    DeleteExpenseContent(
        onConfirmed = model::onConfirmed,
        dismiss = dismiss
    )
}

@Composable
private fun DeleteExpenseContent(
    onConfirmed: () -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = dismiss,
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
                        onClick = dismiss
                    ) {
                        AppText(R.string.dialog_cancel)
                    }

                    Button(
                        onClick = {
                            onConfirmed()
                            dismiss()
                        }
                    ) {
                        AppText(R.string.delete)
                    }
                }
            }
        },
        modifier = modifier,
        title = {
            AppText(
                R.string.delete_expense_dialog_title,
                style = MaterialTheme.typography.h6,
                bold = true
            )
        },
        text = {
            AppText(
                R.string.delete_expense_dialog_confirmation_text,
                style = MaterialTheme.typography.body1
            )
        }
    )
}
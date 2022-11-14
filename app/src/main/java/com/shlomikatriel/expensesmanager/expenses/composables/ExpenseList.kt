package com.shlomikatriel.expensesmanager.expenses.composables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.android.gms.common.internal.Objects
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.composables.AppTextField
import com.shlomikatriel.expensesmanager.compose.composables.Chip
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.expenses.dialogs.DeleteExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.dialogs.UpdateExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.utils.*
import java.text.DecimalFormat

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseList(
    expanded: Boolean,
    month: Int,
    year: Int,
    expenses: ArrayList<Expense>,
    onUpdateExpenseRequest: (expense: Expense) -> Unit,
    onDeleteExpenseRequest: (expense: Expense) -> Unit,
    onCollapseRequest: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.fragment_vertical_spacing)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.expenses_page_title), style = MaterialTheme.typography.h6)
        var offset by remember { mutableStateOf(0f) }
        var selectedTypes by remember { mutableStateOf(emptySet<ExpenseType>()) }
        var searchText by remember { mutableStateOf("") }
        val collapseHandler = {
            onCollapseRequest()
            selectedTypes = emptySet()
            searchText = ""
        }

        BackHandler(expanded, collapseHandler)
        val filteredExpenses = expenses.filteredAndSorted(selectedTypes, searchText)
        val filteredTotal = filteredExpenses.calculateTotal()
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .let { modifier ->
                    if (expanded) {
                        modifier.scrollable(
                            orientation = Orientation.Vertical,
                            state = rememberScrollableState { delta ->
                                offset += delta
                                delta
                            }
                        )
                    } else {
                        modifier
                    }
                }
        ) {
            itemsIndexed(filteredExpenses, key = { _, item -> Objects.hashCode(item.databaseId ?: -1, item.javaClass.kotlin.qualifiedName) }) { index, item ->
                var updateDialogOpened by remember { mutableStateOf(false) }
                var deleteDialogOpened by remember { mutableStateOf(false) }
                val currencyInstance = DecimalFormat.getCurrencyInstance()
                Divider()
                ExpenseItem(
                    editable = expanded,
                    name = item.name,
                    cost = when (item) {
                        is Expense.OneTime -> currencyInstance.format(item.cost)
                        is Expense.Monthly -> stringResource(
                            R.string.expenses_page_recycler_item_monthly,
                            currencyInstance.format(item.cost)
                        )
                        is Expense.Payments -> {
                            val totalMonths = year * 12 + month
                            stringResource(
                                R.string.expenses_page_recycler_item_payments,
                                currencyInstance.format(item.cost / item.payments),
                                totalMonths - item.month + 1,
                                item.payments
                            )
                        }
                    },
                    onUpdate = {
                        updateDialogOpened = true
                    },
                    onDelete = {
                        deleteDialogOpened = true
                    },
                    modifier = Modifier.animateItemPlacement()
                )
                if (index == filteredExpenses.lastIndex) {
                    Divider()
                }
                if (updateDialogOpened) {
                    UpdateExpenseDialog(
                        expense = item,
                        onConfirm = onUpdateExpenseRequest,
                        onDismissRequest = { updateDialogOpened = false }
                    )
                }
                if (deleteDialogOpened) {
                    DeleteExpenseDialog(
                        expense = item,
                        onConfirm = onDeleteExpenseRequest,
                        onDismissRequest = { deleteDialogOpened = false }
                    )
                }
            }
        }

        AnimatedVisibility(expanded) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.fragment_vertical_spacing)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val currencyInstance = DecimalFormat.getCurrencyInstance()
                Box {
                    val animatedTotal by animateFloatAsState(filteredTotal, tween(500, easing = LinearOutSlowInEasing))
                    Text(
                        text = stringResource(
                            R.string.expenses_page_total,
                            currencyInstance.format(animatedTotal)
                        ),
                        style = MaterialTheme.typography.h6
                    )
                }
                FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                    ExpenseType.values().forEach { type ->
                        Chip(
                            title = type.displayText,
                            checked = selectedTypes.contains(type),
                            onCheckedChanged = {
                                val newSelectedFilters = if (it) {
                                    selectedTypes + type
                                } else {
                                    selectedTypes - type
                                }
                                selectedTypes = newSelectedFilters
                            }
                        )
                    }
                }
                AppTextField(searchText, R.string.expenses_page_search, { searchText = it }, Modifier.fillMaxWidth())
                IconButton(onClick = {
                    collapseHandler()
                }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = stringResource(R.string.collapse_expenses_description))
                }
            }
        }
    }
}
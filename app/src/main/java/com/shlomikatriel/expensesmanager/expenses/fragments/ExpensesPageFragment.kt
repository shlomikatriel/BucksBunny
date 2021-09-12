package com.shlomikatriel.expensesmanager.expenses.fragments

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.navArgs
import com.google.accompanist.flowlayout.FlowRow
import com.shlomikatriel.expensesmanager.LocalizationManager
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.compose.composables.Chip
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.expenses.composables.ExpenseItem
import com.shlomikatriel.expensesmanager.expenses.dialogs.DeleteExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.dialogs.UpdateExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesBaseViewModel
import com.shlomikatriel.expensesmanager.logs.logInfo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ExpensesPageState(
    val expenses: ArrayList<Expense> = arrayListOf(),
    val types: Set<ExpenseType> = ExpenseType.values().toSet(),
    val total: String? = null,
    val currencySymbol: String? = null
)

@HiltViewModel
class ExpensesPageViewModel @Inject constructor(
    databaseManager: DatabaseManager,
    sharedPreferences: SharedPreferences,
    private val localizationManager: LocalizationManager
) : ExpensesBaseViewModel(databaseManager, sharedPreferences) {

    var initialized = false

    private val state = mutableStateOf(ExpensesPageState())

    fun getState(): State<ExpensesPageState> = state

    override fun onIncomeChanged(income: Float) {
        // Not needed in pages
    }

    override fun onExpensesChanged(expenses: ArrayList<Expense>) {
        logInfo("Expenses changed (${expenses.size} items)")
        val filteredExpenses = filterExpenses(expenses, state.value.types).sorted()
        val total = calculateTotal(filteredExpenses).toFloat()
        state.value = state.value.copy(
            expenses = filteredExpenses,
            total = localizationManager.getCurrencyFormat().format(total)
        )
    }

    fun initialize(month: Int) {
        if (!initialized) {
            initialized = true
            logInfo("Initializing view model [month=${month % 12}, year=${month / 12}]")
            observeChanges(month)
        }
    }

    fun onFilterToggle(expenseType: ExpenseType, value: Boolean) {
        logInfo("Filter toggle [expenseFilter=$expenseType, value=$value]")
        val types = state.value.types.toMutableSet().apply {
            if (value) {
                add(expenseType)
            } else {
                remove(expenseType)
            }
        }

        val expenses = getExpenses()
        val filteredExpenses = filterExpenses(expenses, types).sorted()
        val total = calculateTotal(filteredExpenses).toFloat()
        state.value = state.value.copy(
            expenses = filteredExpenses,
            types = types,
            total = localizationManager.getCurrencyFormat().format(total)
        )
    }

    private fun filterExpenses(
        expenses: ArrayList<Expense>,
        types: Set<ExpenseType>
    ): ArrayList<Expense> {
        val newExpenses = expenses.filter { types.contains(it.getExpenseType()) }
            .toTypedArray()
        return arrayListOf(*newExpenses)
    }

    private fun ArrayList<Expense>.sorted(): ArrayList<Expense> {
        sortBy { it.timeStamp }
        return this
    }
}

@Preview(
    "Normal",
    showBackground = true,
    locale = "en"
)
@Preview(
    "Custom",
    showBackground = true,
    locale = "iw",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ExpensesPagePreview() = AppTheme {
    val expenses = arrayListOf(
        Expense.OneTime(
            databaseId = null,
            timeStamp = 0L,
            name = "Restaurant",
            cost = 333f,
            costFormatted = "333$",
            month = 0
        ),
        Expense.OneTime(
            databaseId = null,
            timeStamp = 0L,
            name = "Car",
            cost = 4000f,
            costFormatted = "4000$",
            month = 1001
        ),
        Expense.Monthly(
            databaseId = null,
            timeStamp = 0L,
            name = "Hot",
            cost = 20f,
            costFormatted = "20$"
        ),
        Expense.Payments(
            databaseId = null,
            timeStamp = 0L,
            name = "Hot",
            cost = 20f,
            costFormatted = "20$",
            month = 1000,
            payments = 4
        )
    )
    ExpensesPageContent(
        expenses,
        "544$",
        setOf(ExpenseType.ONE_TIME, ExpenseType.MONTHLY),
        1002,
        { _: ExpenseType, _: Boolean -> }
    )
}

@Composable
fun ExpensesPageScreen(month: Int) = AppTheme {
    val model: ExpensesPageViewModel = viewModel(key = month.toString())
    val state by remember { model.getState() }
    model.initialize(month)
    ExpensesPageContent(
        expenses = state.expenses,
        total = state.total,
        types = state.types,
        month,
        model::onFilterToggle
    )
}

@Composable
private fun ExpensesPageContent(
    expenses: ArrayList<Expense>,
    total: String?,
    types: Set<ExpenseType>,
    month: Int,
    onFilterToggle: (type: ExpenseType, value: Boolean) -> Unit
) {
    var offset by remember { mutableStateOf(0f) }
    Column(
        modifier = Modifier
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                }
            ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.fragment_vertical_spacing)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
            Chip(
                title = R.string.expenses_page_chip_one_time,
                initialChecked = types.contains(ExpenseType.ONE_TIME),
                onCheckedChanged = {
                    onFilterToggle(ExpenseType.ONE_TIME, it)
                }
            )
            Chip(
                title = R.string.expenses_page_chip_monthly,
                initialChecked = types.contains(ExpenseType.MONTHLY),
                onCheckedChanged = {
                    onFilterToggle(ExpenseType.MONTHLY, it)
                }
            )
            Chip(
                title = R.string.expenses_page_chip_payments,
                initialChecked = types.contains(ExpenseType.PAYMENTS),
                onCheckedChanged = {
                    onFilterToggle(ExpenseType.PAYMENTS, it)
                }
            )
        }
        LazyColumn {
            items(expenses) {
                var updateDialogOpened by remember { mutableStateOf(false) }
                var deleteDialogOpened by remember { mutableStateOf(false) }
                ExpenseItem(
                    name = it.name,
                    cost = when (it) {
                        is Expense.OneTime -> it.costFormatted
                        is Expense.Monthly -> stringResource(
                            R.string.expenses_page_recycler_item_monthly,
                            it.costFormatted
                        )
                        is Expense.Payments -> stringResource(
                            R.string.expenses_page_recycler_item_payments,
                            it.costFormatted,
                            month - it.month + 1,
                            it.payments
                        )
                    },
                    onUpdate = {
                        updateDialogOpened = true
                    },
                    onDelete = {
                        deleteDialogOpened = true
                    },
                )
                if (updateDialogOpened) {
                    UpdateExpenseDialog(initialExpense = it) {
                        updateDialogOpened = false
                    }
                }
                if (deleteDialogOpened) {
                    DeleteExpenseDialog(expense = it) {
                        deleteDialogOpened = false
                    }
                }
            }
        }
        AppText(
            text = stringResource(
                R.string.expenses_page_total,
                total ?: ""
            ), style = MaterialTheme.typography.h6
        )
    }
}

@AndroidEntryPoint
class ExpensesPageFragment : Fragment() {

    private val args: ExpensesPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            ExpensesPageScreen(args.month)
        }
    }
}
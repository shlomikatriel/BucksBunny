package com.shlomikatriel.expensesmanager.expenses.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppImage
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.compose.tooling.ScreenPreviews
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.expenses.composables.ExpenseList
import com.shlomikatriel.expensesmanager.expenses.composables.ExpensesGraph
import com.shlomikatriel.expensesmanager.expenses.composables.NumberPicker
import com.shlomikatriel.expensesmanager.expenses.dialogs.AddExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesBaseViewModel
import com.shlomikatriel.expensesmanager.expenses.utils.ExpensesUtils
import com.shlomikatriel.expensesmanager.expenses.utils.calculateTotal
import com.shlomikatriel.expensesmanager.expenses.utils.getAddButtonText
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.*
import javax.inject.Inject

data class ExpensesState(
    val month: Int = Calendar.getInstance().get(Calendar.MONTH),
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val income: Float = 0f,
    val total: Float = 0f,
    val expenses: ArrayList<Expense> = arrayListOf(),
)

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    databaseManager: DatabaseManager,
    sharedPreferences: SharedPreferences
) : ExpensesBaseViewModel(databaseManager, sharedPreferences) {

    val state: MutableState<ExpensesState> = mutableStateOf(ExpensesState(income = sharedPreferences.getFloat(FloatKey.INCOME)))

    private var month = Calendar.getInstance().get(Calendar.MONTH)
    private var year = Calendar.getInstance().get(Calendar.YEAR)

    init {
        observeExpenses(month, year)
    }

    override fun onIncomeChanged(income: Float) {
        logInfo("Income changed: ${income.hashCode()}")
        state.value = state.value.copy(income = income)
    }

    override fun onExpensesChanged(expenses: ArrayList<Expense>) {
        logInfo("Expenses changed")
        state.value = state.value.copy(
            total = expenses.calculateTotal(),
            expenses = expenses
        )
    }

    fun onMonthChanged(month: Int) {
        logInfo("Month changed: $month]")
        this.month = month
        state.value = state.value.copy(month = month)
        observeExpenses(month, year)
    }

    fun onYearChanged(year: Int) {
        logInfo("Year changed: $year")
        this.year = year
        state.value = state.value.copy(year = year)
        observeExpenses(month, year)
    }

    fun onAddExpenseRequest(expenseType: ExpenseType, name: String, cost: Float, payments: Int?) {
        logInfo("Adding expense [expenseType=$expenseType]")
        viewModelScope.launch(context = Dispatchers.IO) {
            if (ExpensesUtils.isInputValid(expenseType, name, cost, payments)) {
                val expense = ExpensesUtils.create(expenseType, name, cost, payments, month, year)
                databaseManager.insert(expense)
            }
        }
    }

    fun onUpdateExpenseRequest(expense: Expense) {
        logInfo("Updating expense of id: ${expense.databaseId}")
        viewModelScope.launch(context = Dispatchers.IO) {
            databaseManager.update(expense)
        }
    }

    fun onDeleteExpenseRequest(expense: Expense) {
        logInfo("Deleting expense of id: ${expense.databaseId}")
        viewModelScope.launch(context = Dispatchers.IO) {
            databaseManager.delete(expense)
        }
    }
}

@Composable
private fun ExpensesScreen() {

    // TODO("Start hint animation")
    // TODO("Create options menu with old logic")
    // TODO("Apply onboarding of not shown")
    // TODO("Apply snack bar for in-app update")
    // TODO("Apply in-app review")

    val model: ExpensesViewModel = viewModel()
    val mainState by remember { model.state }

    ExpensesContent(mainState, model::onAddExpenseRequest, model::onUpdateExpenseRequest, model::onDeleteExpenseRequest, model::onMonthChanged, model::onYearChanged)
}

@Composable
private fun ExpensesContent(
    state: ExpensesState,
    onAddExpenseRequest: (expenseType: ExpenseType, name: String, cost: Float, payments: Int?) -> Unit,
    onUpdateExpenseRequest: (expense: Expense) -> Unit,
    onDeleteExpenseRequest: (expense: Expense) -> Unit,
    onMonthChanged: (month: Int) -> Unit,
    onYearChanged: (year: Int) -> Unit
) {
    AddExpenseFab(
        onAddExpenseRequest,
        Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.fragment_padding))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.fragment_vertical_spacing))
        ) {
            var expandedExpenses by remember { mutableStateOf(false) }
            AnimatedVisibility(visible = !expandedExpenses) {
                ExpensesGraph(
                    state.income, state.total,
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .let { modifier -> if (expandedExpenses) modifier else modifier.clickable { expandedExpenses = true } },
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                ExpenseList(
                    expandedExpenses,
                    state.month,
                    state.year,
                    state.expenses,
                    onUpdateExpenseRequest,
                    onDeleteExpenseRequest,
                    {
                        expandedExpenses = false
                    },
                    Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.fragment_padding))
                )
            }
            AnimatedVisibility(visible = !expandedExpenses) {
                MonthSelector(
                    state.month, state.year, onMonthChanged, onYearChanged,
                    Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MonthSelector(
    currentMonth: Int,
    currentYear: Int,
    onMonthChanged: (month: Int) -> Unit,
    onYearChanged: (year: Int) -> Unit,
    modifier: Modifier
) {
    var editMode by remember { mutableStateOf(false) }
    BackHandler(editMode) {
        editMode = false
    }
    Surface(
        modifier = modifier.clickable { editMode = !editMode },
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.fragment_padding))
                .animateContentSize(),
            contentAlignment = Alignment.Center
        ) {
            if (editMode) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NumberPicker(
                            minValue = 0,
                            maxValue = BuildConfig.MONTHS_COUNT - 1,
                            initialValue = currentMonth,
                            displayValues = DateFormatSymbols().months.take(BuildConfig.MONTHS_COUNT).toTypedArray(),
                            onValueChange = onMonthChanged
                        )
                        NumberPicker(
                            minValue = currentYear - 5,
                            maxValue = currentYear + 5,
                            initialValue = currentYear,
                            onValueChange = onYearChanged
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.Clear, contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppText(text = DateFormatSymbols().months[currentMonth], style = MaterialTheme.typography.h6)
                    AppText(text = currentYear.toString(), style = MaterialTheme.typography.h6)
                }
            }
        }
    }

}

@Composable
private fun AddExpenseFab(
    onAddExpenseRequest: (expenseType: ExpenseType, name: String, cost: Float, payments: Int?) -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
        var menuExpanded by remember { mutableStateOf(false) }
        var dialogOfType by remember { mutableStateOf(null as ExpenseType?) }
        // force align to right
        val alignment = if (LocalLayoutDirection.current == LayoutDirection.Rtl) Alignment.BottomStart else Alignment.BottomEnd
        FloatingActionButton(
            onClick = { menuExpanded = true },
            modifier = Modifier
                .padding(all = 16.dp)
                .size(56.dp)
                .align(alignment)
        ) {
            AppImage(image = R.drawable.add, color = MaterialTheme.colors.onSecondary, contentDescription = R.string.add_button_description)
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, modifier = Modifier.align(Alignment.BottomStart)) {
            ExpenseType.values().forEach {
                DropdownMenuItem(
                    onClick = {
                        menuExpanded = false
                        dialogOfType = it
                    }
                ) {
                    AppText(it.getAddButtonText())
                }
                Divider()
            }
        }
        dialogOfType?.let {
            AddExpenseDialog(it, onAddExpenseRequest) { dialogOfType = null }
        }
    }
}

@ScreenPreviews
@Composable
private fun ExpensesPreview() = AppTheme {
    val allExpenses = remember { mutableStateListOf<Expense>() }

    var month by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var year by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    val monthExpenses = allExpenses.filter {
        when (it) {
            is Expense.OneTime -> it.month == year * 12 + month
            is Expense.Monthly -> true
            is Expense.Payments -> {
                val absoluteMonth = year * 12 + month
                absoluteMonth in it.month until it.month + it.payments
            }
        }
    }.let { ArrayList(it) }

    val state = ExpensesState(
        month = month,
        year = year,
        income = 10000f,
        total = monthExpenses.calculateTotal(),
        expenses = monthExpenses
    )
    ExpensesContent(
        state = state,
        onAddExpenseRequest = { expenseType, name, cost, payments ->
            val expenseWithId = when (val expense = ExpensesUtils.create(expenseType, name, cost, payments, month, year)) {
                is Expense.OneTime -> expense.copy(databaseId = System.currentTimeMillis())
                is Expense.Monthly -> expense.copy(databaseId = System.currentTimeMillis())
                is Expense.Payments -> expense.copy(databaseId = System.currentTimeMillis())
            }
            allExpenses.add(expenseWithId)
        },
        onUpdateExpenseRequest = { expenseToUpdate ->
            val expenseToDelete = allExpenses.find { it.databaseId == expenseToUpdate.databaseId }
            if (expenseToDelete != null) {
                allExpenses.remove(expenseToDelete)
                allExpenses.add(expenseToUpdate)
            }
        },
        onDeleteExpenseRequest = {
            allExpenses.remove(it)
        },
        onMonthChanged = { month = it },
        onYearChanged = { year = it }
    )
}

@AndroidEntryPoint
class ExpensesFragment : Fragment(), MenuProvider {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            AppTheme {
                ExpensesScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.expenses_main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.settings) {
            findNavController().navigate(ExpensesFragmentDirections.openSettingsFragment())
            return true
        }
        return false
    }
}
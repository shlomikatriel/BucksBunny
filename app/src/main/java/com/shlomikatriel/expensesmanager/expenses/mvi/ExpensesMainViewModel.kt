package com.shlomikatriel.expensesmanager.expenses.mvi

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.Utils
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ExpensesMainViewModel(application: Application) : ExpensesBaseViewModel(application) {

    @Inject
    lateinit var utils: Utils

    private val viewStateLiveData = MutableLiveData<ExpensesMainViewState>()

    init {
        (application as ExpensesManagerApp).appComponent.inject(this)
    }

    private var viewState = ExpensesMainViewState()
        set(value) {
            field = value
            viewStateLiveData.value = value
        }

    fun getViewState() = viewStateLiveData

    override fun onIncomeChanged(income: Float) {
        resultToViewState(ExpensesMainResult.IncomeChange(income))
    }

    override fun onExpensesChanged(expenses: ArrayList<Expense>) {
        val totalExpenses = calculateTotal(expenses).toFloat()
        resultToViewState(ExpensesMainResult.ExpensesChange(totalExpenses))
    }

    fun postEvent(expensesMainEvent: ExpensesMainEvent) {
        logInfo("Processing event $expensesMainEvent")
        when (expensesMainEvent) {
            is ExpensesMainEvent.Initialize -> handleInitialize()
            is ExpensesMainEvent.MonthChange -> handleMonthChange(expensesMainEvent.newPosition)
        }
    }

    private fun handleInitialize() {
        val position = viewState.forceSelectPage ?: BuildConfig.MAX_MONTHS_OFFSET
        val month = utils.getMonthOfPosition(position)
        observeChanges(month)
        val income = sharedPreferences.getFloat(FloatKey.INCOME)
        val expensesList = getExpenses()
        val expenses = calculateTotal(expensesList).toFloat()
        resultToViewState(ExpensesMainResult.Initialize(position, income, expenses))
    }

    private fun handleMonthChange(newPosition: Int) {
        logDebug("Handling month change event [newPosition=$newPosition")
        if (newPosition in 0 until BuildConfig.MONTHS_COUNT) {
            val month = utils.getMonthOfPosition(newPosition)
            observeChanges(month)
            resultToViewState(ExpensesMainResult.MonthChange(newPosition))
        } else {
            logInfo("New position is not in acceptable range, ignoring event")
        }
    }

    private fun resultToViewState(result: ExpensesMainResult) {
        logDebug("Processing result $result")
        viewState = when (result) {
            is ExpensesMainResult.Initialize -> viewState.copy(
                    time = transformPositionToMonthMillis(result.position),
                    forceSelectPage = result.position,
                    income = result.income,
                    expenses = result.expenses
            )
            is ExpensesMainResult.MonthChange -> viewState.copy(
                    time = transformPositionToMonthMillis(result.newPosition),
                    forceSelectPage = null
            )
            is ExpensesMainResult.IncomeChange -> viewState.copy(
                    income = result.income
            )
            is ExpensesMainResult.ExpensesChange -> viewState.copy(
                    expenses = result.expenses
            )
        }
    }

    private fun transformPositionToMonthMillis(position: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, position - BuildConfig.MAX_MONTHS_OFFSET)
        return calendar.timeInMillis
    }

    override fun onCleared() {
        super.onCleared()
        logInfo("Expenses fragment view model cleared")
    }
}
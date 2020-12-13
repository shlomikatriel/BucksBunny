package com.shlomikatriel.expensesmanager.ui.expenses.mvi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.logs.Logger
import java.util.*

class ExpensesViewModel : ViewModel() {

    private val viewStateLiveData = MutableLiveData<ExpensesViewState>()

    private var viewState =
        ExpensesViewState()
        set(value) {
            field = value
            viewStateLiveData.value = value
        }

    fun getViewState() = viewStateLiveData

    fun postEvent(expensesEvent: ExpensesEvent) {
        Logger.i("Processing event $expensesEvent")
        when (expensesEvent) {
            is ExpensesEvent.InitializeEvent -> resultToViewState(
                ExpensesResult.InitializeResult
            )
            is ExpensesEvent.MonthChangeEvent -> handleMonthChange(expensesEvent.newPosition)
        }
    }

    private fun handleMonthChange(newPosition: Int) {
        Logger.d("Handling month change event [newPosition=$newPosition")
        if (newPosition in 0 until BuildConfig.MONTHS_COUNT) {
            resultToViewState(ExpensesResult.MonthChangeResult(newPosition))
        } else {
            Logger.i("New position is not in acceptable range, ignoring event")
        }
    }

    private fun resultToViewState(expensesResult: ExpensesResult) {
        Logger.d("Processing result $expensesResult")
        viewState = when (expensesResult) {
            ExpensesResult.InitializeResult -> {
                val position = viewState.forceSelectPage ?: BuildConfig.MAX_MONTHS_OFFSET
                viewState.copy(
                    time = transformPositionToMonthMillis(position),
                    forceSelectPage = position
                )
            }
            is ExpensesResult.MonthChangeResult -> {
                viewState.copy(
                    time = transformPositionToMonthMillis(expensesResult.newPosition),
                    forceSelectPage = null
                )
            }
        }
    }

    private fun transformPositionToMonthMillis(position: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, position - BuildConfig.MAX_MONTHS_OFFSET)
        return calendar.timeInMillis
    }

    override fun onCleared() {
        super.onCleared()
        Logger.i("Expenses fragment view model cleared")
    }
}
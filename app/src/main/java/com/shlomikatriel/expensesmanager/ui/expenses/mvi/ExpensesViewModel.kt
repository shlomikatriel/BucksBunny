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
            resultToViewState(
                ExpensesResult.MonthChangeResult(
                    newPosition
                )
            )
        } else {
            Logger.i("New position is not in acceptable range, ignoring event")
        }
    }

    private fun resultToViewState(expensesResult: ExpensesResult) {
        Logger.i("Processing result $expensesResult")

        viewState = when (expensesResult) {
            ExpensesResult.InitializeResult -> {
                viewState.copy(
                    time = System.currentTimeMillis(),
                    selectedPage = BuildConfig.MAX_MONTHS_OFFSET
                )
            }
            is ExpensesResult.MonthChangeResult -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, expensesResult.newPosition - BuildConfig.MAX_MONTHS_OFFSET)
                viewState.copy(
                    time = calendar.timeInMillis,
                    selectedPage = expensesResult.newPosition
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Logger.i("Expenses fragment view model cleared")
    }
}
package com.shlomikatriel.expensesmanager.onboarding

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shlomikatriel.expensesmanager.LocalizationManager
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppInfoText
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.IncomeInput
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.sharedpreferences.putFloat
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class OnboardingIncomeViewState(
    val symbol: String,
    val income: Float
)

@HiltViewModel
class OnboardingIncomeViewModel @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val localizationManager: LocalizationManager
) : ViewModel() {

    val defaultViewState = OnboardingIncomeViewState(
        symbol = localizationManager.getCurrencySymbol(),
        income = sharedPreferences.getFloat(FloatKey.INCOME)
    )

    private val viewState = MutableLiveData(defaultViewState)

    fun getViewState(): LiveData<OnboardingIncomeViewState> = viewState

    fun onIncomeChanged(income: Float) {
        logInfo("Income changed")
        sharedPreferences.putFloat(FloatKey.INCOME, income)
        viewState.value = viewState.value?.copy(income = income)
    }

    override fun onCleared() {
        super.onCleared()
        logInfo("Onboarding income view model cleared")
    }
}

@Preview(
    showBackground = true,
    locale = "en"
)
@Composable
private fun OnboardingIncomeScreenPreview() = AppTheme {
    OnboardingIncomeContent("₪", 3440.3f) {}
}

@Composable
fun OnboardingIncomeScreen(model: OnboardingIncomeViewModel = viewModel()) {
    val state: OnboardingIncomeViewState by model.getViewState()
        .observeAsState(model.defaultViewState)

    OnboardingIncomeContent(state.symbol, state.income, model::onIncomeChanged)
}

@Composable
private fun OnboardingIncomeContent(
    symbol: String,
    income: Float,
    onIncomeChanged: (income: Float) -> Unit
) = Column(
    modifier = Modifier
        .padding(all = dimensionResource(R.dimen.fragment_padding))
        .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(
        dimensionResource(R.dimen.dialog_vertical_spacing),
        Alignment.CenterVertically
    )
) {
    IncomeInput(true, symbol, income, onIncomeChanged)
    AppInfoText(
        text = stringResource(R.string.onboarding_income_input_info),
        colored = true,
        bold = true
    )
}


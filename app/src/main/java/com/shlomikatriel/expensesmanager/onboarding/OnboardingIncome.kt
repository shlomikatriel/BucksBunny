package com.shlomikatriel.expensesmanager.onboarding

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppInfoText
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.preferences.components.IncomeInput
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.sharedpreferences.putFloat
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingIncomeViewModel @Inject constructor(
    val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun getIncome() = sharedPreferences.getFloat(FloatKey.INCOME)

    fun onIncomeChanged(income: Float?) {
        logInfo("Income changed")
        if (income != null) {
            sharedPreferences.putFloat(FloatKey.INCOME, income)
        }
    }

    override fun onCleared() {
        super.onCleared()
        logInfo("Onboarding income view model cleared")
    }
}

@ComponentPreviews
@Composable
private fun OnboardingIncomeScreenPreview() = AppTheme {
    OnboardingIncomeContent(3440.3f) {}
}

@Composable
fun OnboardingIncomeScreen() {
    val model: OnboardingIncomeViewModel = hiltViewModel()
    OnboardingIncomeContent(model.getIncome(), model::onIncomeChanged)
}

@Composable
private fun OnboardingIncomeContent(
    income: Float,
    onIncomeChanged: (income: Float?) -> Unit
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
    IncomeInput(true, income, onIncomeChanged)
    AppInfoText(
        text = R.string.onboarding_income_input_info,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}


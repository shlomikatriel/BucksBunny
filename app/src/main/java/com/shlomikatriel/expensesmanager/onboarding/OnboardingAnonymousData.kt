package com.shlomikatriel.expensesmanager.onboarding

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppInfoText
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.compose.tooling.ScreenPreviews
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.preferences.components.SwitchPreference
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.putBoolean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingAnonymousDataViewModel @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val firebaseCrashlytics: FirebaseCrashlytics,
    val firebaseAnalytics: FirebaseAnalytics
) : ViewModel() {

    val state = mutableStateOf(sharedPreferences.getBoolean(BooleanKey.ANONYMOUS_REPORTS_ENABLED))

    fun onValueChanged(value: Boolean) {
        logInfo("Crashlytics changed: $value")
        sharedPreferences.putBoolean(BooleanKey.ANONYMOUS_REPORTS_ENABLED, value)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(value)
        firebaseAnalytics.setAnalyticsCollectionEnabled(value)
        state.value = value
    }
}

@ScreenPreviews
@Composable
private fun AnonymousDataPreview() = AppTheme {
    var value by remember { mutableStateOf(false) }
    AnonymousDataContent(value) {
        value = it
    }
}

@Composable
fun AnonymousDataScreen() {
    val model: OnboardingAnonymousDataViewModel = viewModel()
    val value by remember { model.state }
    AnonymousDataContent(value, model::onValueChanged)
}

@Composable
private fun AnonymousDataContent(
    value: Boolean,
    onValueChanged: (value: Boolean) -> Unit
) = AppTheme {
    Column(
        modifier = Modifier
            .padding(all = dimensionResource(R.dimen.fragment_padding))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.dialog_vertical_spacing),
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppText(
            text = R.string.preferences_help_us_improve_title,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.primary,
            bold = true
        )
        AppInfoText(R.string.preferences_help_us_improve_info)
        SwitchPreference(
            title = R.string.preferences_help_us_improve_anonymous_data_reports_title,
            description = R.string.preferences_help_us_improve_anonymous_data_reports_description,
            value = value,
            onValueChanged = onValueChanged
        )
    }

}
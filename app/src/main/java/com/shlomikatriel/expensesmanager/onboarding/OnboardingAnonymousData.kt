package com.shlomikatriel.expensesmanager.onboarding

import android.content.SharedPreferences
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppInfoText
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.compose.composables.Permission
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.putBoolean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class OnboardingAnonymousDataViewState(
    val crashlyticsEnabled: Boolean,
    val analyticsEnabled: Boolean
)


@HiltViewModel
class OnboardingAnonymousDataViewModel @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val firebaseCrashlytics: FirebaseCrashlytics,
    val firebaseAnalytics: FirebaseAnalytics
) : ViewModel() {

    val defaultViewState = OnboardingAnonymousDataViewState(
        crashlyticsEnabled = sharedPreferences.getBoolean(BooleanKey.FIREBASE_CRASHLYTICS_ENABLED),
        analyticsEnabled = sharedPreferences.getBoolean(BooleanKey.FIREBASE_ANALYTICS_ENABLED)
    )

    private val viewState = MutableLiveData(defaultViewState)

    fun getViewState(): LiveData<OnboardingAnonymousDataViewState> = viewState

    fun onCrashlyticsChanged(value: Boolean) {
        logInfo("Crashlytics changed: $value")
        sharedPreferences.putBoolean(BooleanKey.FIREBASE_CRASHLYTICS_ENABLED, value)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(value)
        viewState.value = viewState.value?.copy(crashlyticsEnabled = value)
    }

    fun onAnalyticsChanged(value: Boolean) {
        logInfo("Analytics changed: $value")
        sharedPreferences.putBoolean(BooleanKey.FIREBASE_ANALYTICS_ENABLED, value)
        firebaseAnalytics.setAnalyticsCollectionEnabled(value)
        viewState.value = viewState.value?.copy(analyticsEnabled = value)
    }
}

@Preview(
    showBackground = true,
    locale = "en"
)
@Preview(
    showBackground = true,
    locale = "iw",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun AnonymousDataScreenPreview() = AppTheme {
    AnonymousDataContent(
        crashlyticsEnabled = true,
        analyticsEnabled = false,
        {},
        {}
    )
}

@Composable
fun AnonymousDataScreen(model: OnboardingAnonymousDataViewModel = viewModel()) {
    val state: OnboardingAnonymousDataViewState by model.getViewState()
        .observeAsState(model.defaultViewState)

    AnonymousDataContent(
        state.crashlyticsEnabled,
        state.analyticsEnabled,
        model::onCrashlyticsChanged,
        model::onAnalyticsChanged
    )
}

@Composable
private fun AnonymousDataContent(
    crashlyticsEnabled: Boolean,
    analyticsEnabled: Boolean,
    onCrashlyticsChanged: (value: Boolean) -> Unit,
    onAnalyticsChanged: (value: Boolean) -> Unit
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
            text = R.string.settings_anonymous_reports_category_title,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.primary,
            bold = true
        )
        AppInfoText(R.string.settings_anonymous_reports_category_summary)
        Permission(
            title = R.string.settings_anonymous_crash_reports_title,
            description = R.string.settings_anonymous_crash_reports_summary,
            initialValue = crashlyticsEnabled,
            onValueChanged = onCrashlyticsChanged
        )
        Permission(
            title = R.string.settings_anonymous_usage_data_reports_title,
            description = R.string.settings_anonymous_usage_data_reports_summary,
            initialValue = analyticsEnabled,
            onValueChanged = onAnalyticsChanged
        )
    }

}
package com.shlomikatriel.expensesmanager.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.tooling.ScreenPreviews
import com.shlomikatriel.expensesmanager.logs.*
import com.shlomikatriel.expensesmanager.preferences.components.*
import com.shlomikatriel.expensesmanager.preferences.components.base.Preference
import com.shlomikatriel.expensesmanager.preferences.components.base.PreferenceCategory
import com.shlomikatriel.expensesmanager.preferences.utils.DarkMode
import com.shlomikatriel.expensesmanager.preferences.utils.Intents
import com.shlomikatriel.expensesmanager.preferences.utils.Locales
import com.shlomikatriel.expensesmanager.sharedpreferences.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

data class PreferencesState(
    val darkMode: DarkMode,
    val locale: Locale?,
    val income: Float,
    val anonymousReportsEnabled: Boolean,
    val preparingBugReport: Boolean = false
)

interface PreferencesEventHandler {
    fun onDarkModeSelected(darkMode: DarkMode)
    fun onLocaleSelected(locale: Locale?)
    fun onIncomeChanged(income: Float)
    fun onAnonymousReportsChanged(value: Boolean)
    fun onBugReportClicked()
}

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class PreferencesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val logManager: LogManager
) : ViewModel(), PreferencesEventHandler {

    val state: MutableState<PreferencesState>

    init {
        val darkMode = sharedPreferences.getInt(IntKey.DARK_MODE).let {
            DarkMode.fromMode(it)
        }
        val income = sharedPreferences.getFloat(FloatKey.INCOME)
        val anonymousReportsEnabled = sharedPreferences.getBoolean(BooleanKey.ANONYMOUS_REPORTS_ENABLED)
        state = mutableStateOf(PreferencesState(darkMode, Locales.getLocale(), income, anonymousReportsEnabled))
    }

    override fun onDarkModeSelected(darkMode: DarkMode) {
        logInfo("Dark mode setting selected: $darkMode")
        state.value = state.value.copy(darkMode = darkMode)
        sharedPreferences.putInt(IntKey.DARK_MODE, darkMode.mode)
        AppCompatDelegate.setDefaultNightMode(darkMode.mode)
    }

    override fun onLocaleSelected(locale: Locale?) {
        logInfo("Locale selected: $locale")
        state.value = state.value.copy(locale = locale)
        Locales.setLocale(locale)
    }

    override fun onIncomeChanged(income: Float) {
        logInfo("Income changed")
        sharedPreferences.putFloat(FloatKey.INCOME, income)
        state.value = state.value.copy(income = income)
    }

    override fun onAnonymousReportsChanged(value: Boolean) {
        logInfo("Anonymous reports changed: $value")
        sharedPreferences.putBoolean(BooleanKey.ANONYMOUS_REPORTS_ENABLED, value)
        firebaseAnalytics.setAnalyticsCollectionEnabled(value)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(value)
        state.value = state.value.copy(anonymousReportsEnabled = value)
    }

    override fun onBugReportClicked() {
        logInfo("Report a bug clicked")
        viewModelScope.launch {
            state.value = state.value.copy(preparingBugReport = true)
            delay(2000)
            withContext(Dispatchers.IO) {
                try {
                    val file = logManager.collectLogs()
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        file
                    )
                    val intent = Intents.send(
                        mailAddress = BuildConfig.MAIL_ADDRESS,
                        subject = context.getString(R.string.preferences_bug_report_subject),
                        text = context.getString(R.string.preferences_bug_report_content),
                        fileUri = uri
                    )
                    val chooserIntent = Intent.createChooser(intent, context.getString(R.string.preferences_bug_report)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(chooserIntent)
                    logInfo("Bug report prepared")
                } catch (e: Exception) {
                    logError("Failed to prepare bug report", e)
                }
            }
            state.value = state.value.copy(preparingBugReport = false)
        }
    }

    override fun onCleared() {
        logDebug("Clearing settings view model")
    }
}

@Composable
fun PreferencesScreen() {
    val model: PreferencesViewModel = hiltViewModel()
    val state by remember { model.state }
    PreferencesContent(state, model)
}

@Composable
private fun PreferencesContent(state: PreferencesState, eventHandler: PreferencesEventHandler) {
    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.fragment_padding))
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.fragment_vertical_spacing))
    ) {
        Spacer(modifier = Modifier.weight(1f))
        PreferenceCategory(title = R.string.preferences_appearance_title) {
            DarkModePreference(state.darkMode, eventHandler::onDarkModeSelected)
            Divider()
            LocalePreference(state.locale, eventHandler::onLocaleSelected)
        }
        PreferenceCategory(title = R.string.preferences_functionality_title) {
            IncomePreference(state.income, eventHandler::onIncomeChanged)
        }
        PreferenceCategory(title = R.string.preferences_help_us_improve_title, info = R.string.preferences_help_us_improve_info) {
            AnonymousReportsSetting(state.anonymousReportsEnabled, eventHandler::onAnonymousReportsChanged)
            Divider()
            BugReportPreference(state.preparingBugReport, eventHandler::onBugReportClicked)
        }
        PreferenceCategory(title = R.string.preferences_about_category_title) {
            val context = LocalContext.current
            Preference(title = R.string.preferences_application_info) {
                context.startActivity(Intents.appInfo())
            }
            Divider()
            Preference(title = R.string.preferences_github_title) {
                context.startActivity(Intents.view(BuildConfig.GITHUB_URL))
            }
            Divider()
            Preference(title = R.string.preferences_apache_license_title) {
                context.startActivity(Intents.view(BuildConfig.APACHE_LICENSE_URL))
            }
            Divider()
            Preference(title = R.string.preferences_open_source_licenses) {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AnonymousReportsSetting(value: Boolean, onValueChanged: (value: Boolean) -> Unit) = SwitchPreference(
    title = R.string.preferences_help_us_improve_anonymous_data_reports_title,
    description = R.string.preferences_help_us_improve_anonymous_data_reports_description,
    value = value,
    onValueChanged = onValueChanged
)

@Composable
private fun BugReportPreference(preparing: Boolean, onClick: () -> Unit) = if (preparing) {
    Preference(title = R.string.preferences_bug_report, subtitle = R.string.preferences_bug_report_preparing_report) { }
} else {
    Preference(title = R.string.preferences_bug_report, onClick = onClick)
}

//private fun Locale.getDisplayText(): String {
////    return "${Currency.getInstance(this).symbol} ($displayLanguage, $displayCountry)"
//    return "$displayLanguage, $displayCountry"
//}

@ScreenPreviews
@Composable
private fun PreferencesPreview() = AppTheme {
    val initialState = PreferencesState(
        darkMode = DarkMode.SYSTEM_DEFAULT,
        locale = null,
        15000f,
        anonymousReportsEnabled = false
    )
    var state by remember { mutableStateOf(initialState) }
    val coroutineScope = rememberCoroutineScope()
    val eventHandler = object : PreferencesEventHandler {
        override fun onDarkModeSelected(darkMode: DarkMode) {
            state = state.copy(darkMode = darkMode)
        }

        override fun onLocaleSelected(locale: Locale?) {
            state = state.copy(locale = locale)
        }

        override fun onIncomeChanged(income: Float) {
            state = state.copy(income = income)
        }

        override fun onAnonymousReportsChanged(value: Boolean) {
            state = state.copy(anonymousReportsEnabled = value)
        }

        override fun onBugReportClicked() {
            coroutineScope.launch {
                state = state.copy(preparingBugReport = true)
                delay(3000L)
                state = state.copy(preparingBugReport = false)
            }
        }

    }
    PreferencesContent(state, eventHandler)
}
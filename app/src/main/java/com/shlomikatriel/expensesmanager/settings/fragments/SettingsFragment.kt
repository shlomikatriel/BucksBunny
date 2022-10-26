package com.shlomikatriel.expensesmanager.settings.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.LocalizationManager
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.configureToolbar
import com.shlomikatriel.expensesmanager.logs.LogManager
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logError
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.navigation.navigate
import com.shlomikatriel.expensesmanager.settings.AppDataStore
import com.shlomikatriel.expensesmanager.settings.fragments.SettingsFragmentDirections.Companion.openChooseIncomeDialog
import com.shlomikatriel.expensesmanager.settings.fragments.SettingsFragmentDirections.Companion.openOpenSourceLicensesActivity
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : SharedPreferences.OnSharedPreferenceChangeListener,
    PreferenceFragmentCompat() {

    companion object {
        const val KEY_DARK_MODE = "dark_mode"
        const val CURRENCY = "currency"
        const val MONTHLY_INCOME_KEY = "monthly_income"
        const val ANONYMOUS_CRASH_REPORTS_KEY = "anonymous_crash_reports"
        const val ANONYMOUS_USAGE_DATA_KEY = "anonymous_usage_data"
        const val APPLICATION_INFO_KEY = "application_info"
        const val REPORT_BUG_KEY = "report_bug"
        const val GITHUB_KEY = "github"
        const val APACHE_LICENSE_KEY = "apache_license"
        const val OPEN_SOURCE_LICENSES_KEY = "open_source_licenses"

        const val MAIL_ADDRESS = "shlomikatriel@gmail.com"
        const val GITHUB_URL = "https://github.com/shlomikatriel/ExpensesManager"
        const val APACHE_LICENSE_URL =
            "https://raw.githubusercontent.com/shlomikatriel/ExpensesManager/develop/LICENSE"
    }

    @ApplicationContext
    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var appDataStore: AppDataStore

    @Inject
    lateinit var localizationManager: LocalizationManager

    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    private var job: Job? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = appDataStore

        setPreferencesFromResource(R.xml.preferences, rootKey)

        populateLocalizationEntries()

        updateMonthlyIncomeSummary()
    }

    override fun onResume() {
        super.onResume()
        logDebug("Settings fragment resumed, registering fragment for shared preferences changes")
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
        super.onPause()
        logDebug("Settings fragment paused, unregistering fragment for shared preferences changes")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == FloatKey.INCOME.getKey()) {
            logInfo("Monthly income changed, updating summary")
            updateMonthlyIncomeSummary()
        }
    }

    private fun populateLocalizationEntries() {
        preferenceManager.findPreference<ListPreference>(CURRENCY)?.let { it ->
            val supportedLocales = localizationManager.getAllSupportedLocales()
            it.entries = mutableListOf(getString(R.string.settings_system_default)).apply {
                addAll(supportedLocales.map { locale ->
                    "${Currency.getInstance(locale).symbol} (${locale.displayLanguage}, ${locale.displayCountry})"
                })
            }.toTypedArray()
            it.entryValues = mutableListOf("").apply {
                addAll(supportedLocales.map { locale -> locale.toString() })
            }.toTypedArray()
        }
    }

    private fun updateMonthlyIncomeSummary() {
        preferenceManager.findPreference<Preference>(MONTHLY_INCOME_KEY)?.summary =
            DecimalFormat.getCurrencyInstance().format(sharedPreferences.getFloat(FloatKey.INCOME))
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        logInfo("Preference ${preference.key} clicked")
        try {
            when (preference.key) {
                MONTHLY_INCOME_KEY -> navigate(openChooseIncomeDialog())
                REPORT_BUG_KEY -> handleReportBugClick()
                APPLICATION_INFO_KEY -> handleApplicationInfoClicked()
                GITHUB_KEY -> openWebPage(GITHUB_URL)
                APACHE_LICENSE_KEY -> openWebPage(APACHE_LICENSE_URL)
                OPEN_SOURCE_LICENSES_KEY -> navigate(openOpenSourceLicensesActivity())
                else -> return super.onPreferenceTreeClick(preference)
            }
            return true
        } catch (e: Exception) {
            logError("Failed to process preference ${preference.key} click", e)
            firebaseCrashlytics.recordException(e)
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logDebug("Creating settings fragment view")
        configureToolbar()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun handleReportBugClick() {
        if (job?.isCompleted != false) {
            logInfo("Starting log collection job")
            job = lifecycleScope.launch {
                val file = logManager.collectLogs()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(MAIL_ADDRESS))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_bug_report_subject))
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_bug_report_content))
                    val uri = FileProvider.getUriForFile(
                        appContext,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        file
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                }
                startActivity(Intent.createChooser(intent, getString(R.string.settings_bug_report)))
                logInfo("Log collection job done")
            }
        } else {
            logInfo("Active log collection job is not complete [completed=${job?.isCompleted}]")
        }
    }

    private fun handleApplicationInfoClicked() = try {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.settings_open_source_licenses))
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        }
        startActivity(intent)
    } catch (e: Exception) {
        logError("Failed to open application info", e)
        firebaseCrashlytics.recordException(e)
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        logInfo("Settings fragment stopped [isJobCompleted=${job?.isCompleted}]")
        job?.takeIf { !it.isCompleted }?.cancel(CancellationException("Settings fragment stopped"))
    }
}
package com.shlomikatriel.expensesmanager.ui.settings.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.extensions.safeNavigate
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.ui.configureToolbar
import com.shlomikatriel.expensesmanager.ui.settings.AppDataStore
import com.shlomikatriel.expensesmanager.ui.settings.fragments.SettingsFragmentDirections.Companion.openChooseIncomeDialog
import com.shlomikatriel.expensesmanager.ui.settings.fragments.SettingsFragmentDirections.Companion.openOpenSourceLicensesActivity
import java.text.DecimalFormat
import javax.inject.Inject


class SettingsFragment: SharedPreferences.OnSharedPreferenceChangeListener, PreferenceFragmentCompat() {

    companion object {
        const val KEY_DARK_MODE = "dark_mode"
        const val MONTHLY_INCOME_KEY = "monthly_income"
        const val OPEN_SOURCE_LICENSES_KEYS = "open_source_licenses"
        const val SEND_MAIL_KEY = "send_mail"
        const val MAIL_ADDRESS = "shlomikatriel@gmail.com"
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)

        preferenceManager.preferenceDataStore = AppDataStore(sharedPreferences)

        setPreferencesFromResource(R.xml.preferences, rootKey)

        updateMonthlyIncomeSummary()
    }

    override fun onResume() {
        super.onResume()
        Logger.d("Settings fragment resumed, registering fragment for shared preferences changes")
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
        super.onPause()
        Logger.d("Settings fragment paused, unregistering fragment for shared preferences changes")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == FloatKey.INCOME.getKey()) {
            Logger.i("Monthly income changed, updating summary")
            updateMonthlyIncomeSummary()
        }
    }

    private fun updateMonthlyIncomeSummary() {
        preferenceManager.findPreference<Preference>(MONTHLY_INCOME_KEY)?.summary = DecimalFormat.getCurrencyInstance().format(sharedPreferences.getFloat(FloatKey.INCOME))
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        Logger.i("Preference ${preference?.key} clicked")
        when (preference?.key) {
            MONTHLY_INCOME_KEY -> findNavController().safeNavigate(openChooseIncomeDialog(fromOnBoarding = false))
            SEND_MAIL_KEY -> handleSendMailClick()
            OPEN_SOURCE_LICENSES_KEYS -> findNavController().safeNavigate(openOpenSourceLicensesActivity())
            else -> return super.onPreferenceTreeClick(preference)
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Logger.v("Creating settings fragment view")
        configureToolbar(R.string.settings_title, true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun handleSendMailClick() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_EMAIL, MAIL_ADDRESS)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_send_mail_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_send_mail_content))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.settings_send_mail)))
    }
}
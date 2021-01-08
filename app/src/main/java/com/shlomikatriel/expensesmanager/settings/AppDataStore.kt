package com.shlomikatriel.expensesmanager.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceDataStore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.logs.logVerbose
import com.shlomikatriel.expensesmanager.settings.fragments.SettingsFragment
import com.shlomikatriel.expensesmanager.sharedpreferences.*
import javax.inject.Inject

class AppDataStore
@Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : PreferenceDataStore() {

    override fun getString(key: String?, defValue: String?): String? {
        logVerbose("Get string [key=$key, defValue=$defValue]")
        val value = when (key) {
            SettingsFragment.KEY_DARK_MODE -> sharedPreferences.getInt(IntKey.DARK_MODE).toString()
            else -> super.getString(key, defValue)
        }
        logVerbose("Got value: $value")
        return value
    }

    override fun putString(key: String?, value: String?) {
        logVerbose("Put string [key=$key, value=$value]")
        when (key) {
            SettingsFragment.KEY_DARK_MODE -> {
                val valueAsInt = value?.toIntOrNull() ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                sharedPreferences.putInt(IntKey.DARK_MODE, valueAsInt)
                AppCompatDelegate.setDefaultNightMode(valueAsInt)
            }
            else -> super.putString(key, value)
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        logVerbose("Get boolean [key=$key, defValue=$defValue]")
        return when (key) {
            SettingsFragment.ANONYMOUS_USAGE_DATA_KEY -> sharedPreferences.getBoolean(BooleanKey.FIREBASE_ANALYTICS_ENABLED)
            SettingsFragment.ANONYMOUS_CRASH_REPORTS_KEY -> sharedPreferences.getBoolean(BooleanKey.FIREBASE_CRASHLYTICS_ENABLED)
            else -> super.getBoolean(key, defValue)
        }
    }

    override fun putBoolean(key: String?, value: Boolean) {
        logVerbose("Put boolean [key=$key, value=$value]")
        when (key) {
            SettingsFragment.ANONYMOUS_USAGE_DATA_KEY -> {
                sharedPreferences.putBoolean(BooleanKey.FIREBASE_ANALYTICS_ENABLED, value)
                firebaseAnalytics.setAnalyticsCollectionEnabled(value)
            }
            SettingsFragment.ANONYMOUS_CRASH_REPORTS_KEY -> {
                sharedPreferences.putBoolean(BooleanKey.FIREBASE_CRASHLYTICS_ENABLED, value)
                firebaseCrashlytics.setCrashlyticsCollectionEnabled(value)
            }
            else -> super.putBoolean(key, value)
        }
    }
}
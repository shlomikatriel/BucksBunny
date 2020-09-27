package com.shlomikatriel.expensesmanager.ui.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceDataStore
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.sharedpreferences.IntKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getInt
import com.shlomikatriel.expensesmanager.sharedpreferences.putInt
import com.shlomikatriel.expensesmanager.ui.settings.fragments.SettingsFragment

class AppDataStore(val sharedPreferences: SharedPreferences) : PreferenceDataStore() {


    override fun getString(key: String?, defValue: String?): String? {
        Logger.v("Get string [key=$key, defValue=$defValue]")
        val value = when (key) {
            SettingsFragment.KEY_DARK_MODE -> sharedPreferences.getInt(IntKey.DARK_MODE).toString()
            else -> super.getString(key, defValue)
        }
        Logger.v("Got value: $value")
        return value
    }

    override fun putString(key: String?, value: String?) {
        Logger.v("Put string [key=$key, value=$value]")
        return when (key) {
            SettingsFragment.KEY_DARK_MODE -> {
                val valueAsInt = value?.toIntOrNull() ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                sharedPreferences.putInt(IntKey.DARK_MODE, valueAsInt)
                AppCompatDelegate.setDefaultNightMode(valueAsInt)
            }
            else -> super.putString(key, value)
        }
    }
}
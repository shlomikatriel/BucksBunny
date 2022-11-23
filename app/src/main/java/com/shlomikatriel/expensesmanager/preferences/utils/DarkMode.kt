package com.shlomikatriel.expensesmanager.preferences.utils

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.shlomikatriel.expensesmanager.R

enum class DarkMode(
    @StringRes val text: Int,
    val mode: Int
) {
    SYSTEM_DEFAULT(R.string.preferences_system_default, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    ALWAYS(R.string.preferences_dark_mode_always, AppCompatDelegate.MODE_NIGHT_YES),
    NEVER(R.string.preferences_dark_mode_never, AppCompatDelegate.MODE_NIGHT_NO);

    companion object {
        fun fromMode(mode: Int) = values().find { mode == it.mode } ?: SYSTEM_DEFAULT
    }
}
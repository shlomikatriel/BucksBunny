package com.shlomikatriel.expensesmanager.preferences.components

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.preferences.components.base.Preference
import com.shlomikatriel.expensesmanager.preferences.utils.Intents
import java.util.*

@Composable
fun LocalePreference() {
    var locale by remember { mutableStateOf(Locale.getDefault()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        locale = Locale.getDefault()
    }
    val title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) R.string.preferences_app_language else R.string.preferences_device_language
    Preference(title = title, subtitle = locale.getDisplayText()) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Intents.appLocaleSettings() else Intents.localeSettings()
        launcher.launch(intent)
    }
}

fun Locale.getDisplayText(): String {
    return "$displayLanguage, $displayCountry (${Currency.getInstance(this).symbol})"
}
package com.shlomikatriel.expensesmanager.preferences.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.preferences.components.base.Preference
import com.shlomikatriel.expensesmanager.preferences.utils.Locales
import java.util.*


@Composable
fun LocalePreference(
    locale: Locale?,
    onSelected: (currencyLocale: Locale?) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        var menuExpanded by remember { mutableStateOf(false) }
        val displayText = locale?.let { Locales.getDisplayText(it) } ?: stringResource(R.string.preferences_system_default)
        Preference(title = R.string.preferences_language_and_currency, subtitle = displayText) {
            menuExpanded = true
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    onSelected(null)
                },
                text = {
                    Text(stringResource(R.string.preferences_system_default))
                }
            )
            Locales.getAvailableLocales().forEach {
                DropdownMenuItem(
                    onClick = {
                        menuExpanded = false
                        onSelected(it)
                    },
                    text = {
                        Text(Locales.getDisplayText(it))
                    }
                )
            }
        }
    }

}
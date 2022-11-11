package com.shlomikatriel.expensesmanager.preferences.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.preferences.components.base.Preference
import com.shlomikatriel.expensesmanager.preferences.utils.DarkMode

@Composable
fun DarkModePreference(darkMode: DarkMode, onSelected: (darkMode: DarkMode) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        var menuExpanded by remember { mutableStateOf(false) }
        Preference(title = R.string.preferences_dark_mode_title, subtitle = darkMode.text) {
            menuExpanded = true
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            DarkMode.values().forEach {
                DropdownMenuItem(onClick = {
                    menuExpanded = false
                    onSelected(it)
                }) {
                    AppText(it.text)
                }
            }
        }
    }
}
package com.shlomikatriel.expensesmanager.preferences.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews

@ComponentPreviews
@Composable
private fun SwitchPreferencePreview() = AppTheme {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SwitchPreference(
            title = R.string.preferences_anonymous_crash_reports_title,
            description = R.string.preferences_anonymous_crash_reports_summary,
            value = false,
            onValueChanged = { }
        )
    }
}


@Composable
fun SwitchPreference(
    @StringRes title: Int,
    @StringRes description: Int,
    value: Boolean,
    onValueChanged: (value: Boolean) -> Unit
) = Row(
    modifier = Modifier.clickable { onValueChanged(!value) }.padding(8.dp).fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AppText(
            text = title,
            style = MaterialTheme.typography.body1,
            bold = true,
            textAlign = TextAlign.Start
        )
        AppText(
            text = description,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start
        )
    }
    Switch(
        checked = value,
        onCheckedChange = null
    )
}
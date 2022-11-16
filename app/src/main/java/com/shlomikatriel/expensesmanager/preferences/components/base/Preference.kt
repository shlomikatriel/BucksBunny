package com.shlomikatriel.expensesmanager.preferences.components.base

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews

@Composable
fun Preference(
    @StringRes title: Int,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(stringResource(title))
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun Preference(
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onClick: () -> Unit
) {
    Preference(title, stringResource(subtitle), onClick)
}

@ComponentPreviews
@Composable
private fun PreferencePreview() = AppTheme {
    Preference(
        title = R.string.preferences_dark_mode_title,
        subtitle = R.string.preferences_dark_mode_always
    ) {
    }
}
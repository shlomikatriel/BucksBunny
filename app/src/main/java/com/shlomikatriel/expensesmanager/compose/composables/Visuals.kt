package com.shlomikatriel.expensesmanager.compose.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews

@ComponentPreviews
@Composable
private fun ChipPreview() = AppTheme {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Chip(
            R.string.common_google_play_services_enable_title,
            true,
            {}
        )
        Chip(
            R.string.common_google_play_services_enable_title,
            false,
            {}
        )
    }
}

@ComponentPreviews
@Composable
private fun SingleChipPreview() = AppTheme {
    Chip(
        R.string.common_google_play_services_enable_title,
        false,
        {},
        Modifier.padding(4.dp)
    )
}

@Composable
fun Chip(
    @StringRes title: Int,
    checked: Boolean,
    onCheckedChanged: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = {
                    onCheckedChanged(it)
                }
            ),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        color = if (checked) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Text(
            stringResource(title),
            modifier = Modifier.padding(all = 8.dp),
            color = if (checked) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
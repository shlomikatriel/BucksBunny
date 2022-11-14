package com.shlomikatriel.expensesmanager.preferences.components.base

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppInfoText
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews

@Composable
fun PreferenceCategory(
    @StringRes title: Int,
    @StringRes info: Int? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(title), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h5)
            if (info != null) {
                AppInfoText(text = info)
            }
            AnimatedVisibility(expanded) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider()
                    content()
                    Divider()
                }
            }
        }
    }
}


@ComponentPreviews
@Composable
private fun PreferenceCategoryPreview() = AppTheme {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreferenceCategory(
            title = R.string.preferences_anonymous_reports_category_title,
            info = R.string.preferences_help_us_improve_info
        ) {
            Text(stringResource(R.string.preferences_anonymous_usage_data_reports_title), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6)
            Text(stringResource(R.string.preferences_anonymous_usage_data_reports_summary))
        }
        PreferenceCategory(title = R.string.preferences_appearance_title) {
            Text(stringResource(R.string.preferences_dark_mode_title), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6)
            Text(stringResource(R.string.preferences_dark_mode_always))
        }
    }
}
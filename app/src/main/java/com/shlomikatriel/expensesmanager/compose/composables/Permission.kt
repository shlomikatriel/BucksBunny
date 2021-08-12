package com.shlomikatriel.expensesmanager.compose.composables

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppText
import com.shlomikatriel.expensesmanager.compose.AppTheme

@Preview(
    "Normal",
    showBackground = true,
    locale = "en"
)
@Preview(
    "Night",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "iw"
)
@Composable
private fun PermissionPreview() = AppTheme {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dp(8f))
    ) {
        Permission(
            title = R.string.settings_anonymous_crash_reports_title,
            description = R.string.settings_anonymous_crash_reports_summary,
            initialValue = false,
            onValueChanged = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun Permission(
    @StringRes title: Int,
    @StringRes description: Int,
    initialValue: Boolean,
    onValueChanged: (value: Boolean) -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dp(4f))
) {
    Column(
        modifier = modifier.weight(1f, false),
        verticalArrangement = Arrangement.spacedBy(Dp(4f))
    ) {
        AppText(
            text = stringResource(title),
            style = MaterialTheme.typography.body1,
            bold = true,
            textAlign = TextAlign.Start
        )
        AppText(
            text = stringResource(description),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start
        )
    }
    Switch(
        checked = initialValue,
        onCheckedChange = onValueChanged
    )
}
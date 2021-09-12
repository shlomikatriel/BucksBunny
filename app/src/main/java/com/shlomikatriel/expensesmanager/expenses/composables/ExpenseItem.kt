package com.shlomikatriel.expensesmanager.expenses.composables

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppImage
import com.shlomikatriel.expensesmanager.compose.composables.AppText

@Preview(
    name = "Normal",
    showBackground = true,
    locale = "us"
)
@Preview(
    name = "Custom",
    showBackground = true,
    locale = "iw",
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ExpensesItemPreview() = AppTheme {
    ExpenseItem("Restaurant", "365$", { }, { })
}

@Composable
fun ExpenseItem(
    name: String,
    cost: String,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier.padding(all = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
) {
    Column(
        modifier = Modifier.weight(1f, true),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AppText(
            text = name,
            style = MaterialTheme.typography.body1,
            bold = true,
            textAlign = TextAlign.Start
        )
        AppText(
            text = cost,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Start
        )
    }
    Box {
        var menuExpanded by remember { mutableStateOf(false) }
        AppImage(
            R.drawable.more,
            modifier = Modifier.clickable { menuExpanded = true },
            contentDescription = R.string.expenses_page_recycler_item_menu_description
        )
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    onUpdate()
                },
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                AppImage(
                    R.drawable.edit,
                    contentDescription = R.string.expense_menu_delete_description
                )
                AppText(R.string.update)
            }
            Divider()
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    onDelete()
                },
                modifier = Modifier.padding(2.dp)
            ) {
                AppImage(
                    R.drawable.delete,
                    contentDescription = R.string.expense_menu_delete_description,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(end = 4.dp)
                )
                AppText(R.string.delete)
            }
        }
    }
}
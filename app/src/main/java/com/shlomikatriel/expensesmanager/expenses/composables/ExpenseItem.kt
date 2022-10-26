package com.shlomikatriel.expensesmanager.expenses.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppImage
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews

@ComponentPreviews
@Composable
private fun ExpensesItemPreview() = AppTheme {
    ExpenseItem(true, "Restaurant", "365$", { }, { })
}

@Composable
fun ExpenseItem(
    editable: Boolean,
    name: String,
    cost: String,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = if (editable) {
            modifier.clickable {
                menuExpanded = true
            }
        } else {
            modifier
        },
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
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
            if (editable) {
                AppImage(
                    R.drawable.chevron,
                    modifier = Modifier.clickable { menuExpanded = true },
                    contentDescription = R.string.expenses_page_recycler_item_menu_description
                )
            }
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    onUpdate()
                }
            ) {
                AppImage(
                    R.drawable.edit,
                    contentDescription = R.string.expense_menu_update_description
                )
                AppText(R.string.update)
            }
            Divider()
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    onDelete()
                }
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
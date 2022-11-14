package com.shlomikatriel.expensesmanager.expenses.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
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
    Box(
        modifier = if (editable) {
            modifier.clickable {
                menuExpanded = true
            }
        } else {
            modifier
        }
            .padding(4.dp)
            .fillMaxWidth()
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
                Text(
                    text = name,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = cost,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start
                )
            }
            if (editable) {
                val icon = if (LocalLayoutDirection.current == LayoutDirection.Ltr) Icons.Filled.ChevronRight else Icons.Filled.ChevronLeft
                Icon(icon, stringResource(R.string.expenses_page_recycler_item_menu_description))
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
                Icon(Icons.Filled.Edit, stringResource(R.string.expense_menu_update_description))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.update))
            }
            Divider()
            DropdownMenuItem(
                onClick = {
                    menuExpanded = false
                    onDelete()
                }
            ) {
                Icon(Icons.Filled.Delete, stringResource(R.string.expense_menu_delete_description), tint = MaterialTheme.colors.error)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.delete))
            }
        }
    }
}
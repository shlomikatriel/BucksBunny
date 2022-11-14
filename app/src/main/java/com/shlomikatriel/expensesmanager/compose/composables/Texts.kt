package com.shlomikatriel.expensesmanager.compose.composables

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AppInfoText(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    color: Color = LocalContentColor.current
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
) {
    Icon(Icons.Outlined.Info, contentDescription = null, tint = color)
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.body2,
        color = color,
        fontWeight = fontWeight,
        textAlign = TextAlign.Start
    )
}

@Composable
fun AppTextField(
    value: String,
    @StringRes label: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    valueValidator: (input: String) -> Int? = { null },
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: String? = null
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val errorStringRes = valueValidator(value)
    OutlinedTextField(
        value,
        onValueChange,
        label = { Text(stringResource(label)) },
        colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground),
        isError = errorStringRes != null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = { trailingIcon?.let { Text(text = it) } },
        singleLine = true
    )
    val errorMessage = errorStringRes?.let { stringResource(it) } ?: ""
    Text(
        text = errorMessage,
        modifier = Modifier.padding(top = 4.dp)
            .animateContentSize()
            .run {
                if (errorStringRes != null) {
                    wrapContentHeight()
                } else {
                    height(0.dp)
                }
            },
        color = MaterialTheme.colors.error
    )
}
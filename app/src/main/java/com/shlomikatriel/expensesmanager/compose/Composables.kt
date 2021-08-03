package com.shlomikatriel.expensesmanager.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

private val appLightColors = lightColors(
    primary = Color(0xff0288d1),
    primaryVariant = Color(0xff0288d1),
    secondary = Color(0xff9c27b0),
    secondaryVariant = Color(0xff9c27b0)
)

private val appDarkColors = darkColors(
    primary = Color(0xff005b9f),
    primaryVariant = Color(0xff005b9f),
    secondary = Color(0xff6a0080),
    secondaryVariant = Color(0xff6a0080)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    MaterialTheme(
        colors = if (darkTheme) appDarkColors else appLightColors,
        content = content
    )
}

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = LocalTextStyle.current,
    bold: Boolean = false,
    colored: Boolean = false
) = Text(
    text = text,
    style = style,
    color = if (colored) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
    fontWeight = if (bold) FontWeight.Bold else null,
    modifier = modifier
)
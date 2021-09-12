package com.shlomikatriel.expensesmanager.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val appLightColors = lightColors(
    primary = Color(0xff0288d1),
    primaryVariant = Color(0xff01579b),
    secondary = Color(0xff9c27b0),
    secondaryVariant = Color(0xff7b1fa2),
    onBackground = Color.DarkGray,
)

private val appDarkColors = darkColors(
    primary = Color(0xff005b9f),
    primaryVariant = Color(0xff002f6c),
    secondary = Color(0xff6a0080),
    secondaryVariant = Color(0xff4a0072),
    onBackground = Color.LightGray,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    MaterialTheme(
        colors = if (darkTheme) appDarkColors else appLightColors,
        content = content
    )
}
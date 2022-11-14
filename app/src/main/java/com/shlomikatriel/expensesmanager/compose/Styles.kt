package com.shlomikatriel.expensesmanager.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Teal300 = Color(0xff4db6ac)
private val Teal400 = Color(0xff26a69a)
private val Teal700 = Color(0xff00796b)
private val Teal800 = Color(0xff00695c)

private val Cyan400 = Color(0xff26c6da)
private val Cyan600 = Color(0xff00acc1)

private val LightColors = lightColors(
    primary = Teal300,
    primaryVariant = Teal400,
    secondary = Cyan400,
    secondaryVariant = Cyan400
)

private val DarkColors = darkColors(
    primary = Teal800,
    primaryVariant = Teal700,
    secondary = Cyan600,
    secondaryVariant = Cyan600
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
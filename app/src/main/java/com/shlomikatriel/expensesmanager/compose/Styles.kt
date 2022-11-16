package com.shlomikatriel.expensesmanager.compose

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LightColorScheme = lightColorScheme(
    primary = Color(0xff00658f),
    onPrimary = Color(0xffffffff),
    primaryContainer = Color(0xffc8e6ff),
    onPrimaryContainer = Color(0xff001e2e),
    secondary = Color(0xff4f616e),
    onSecondary = Color(0xffffffff),
    secondaryContainer = Color(0xffd2e5f5),
    onSecondaryContainer = Color(0xff0b1d29),
    tertiary = Color(0xff63597c),
    onTertiary = Color(0xffffffff),
    tertiaryContainer = Color(0xffe9ddff),
    onTertiaryContainer = Color(0xff1f1635),
    error = Color(0xffba1a1a),
    onError = Color(0xffffffff),
    errorContainer = Color(0xffffdad6),
    onErrorContainer = Color(0xff410002),
    background = Color(0xfffcfcff),
    onBackground = Color(0xff191c1e),
    surface = Color(0xfffcfcff),
    onSurface = Color(0xff191c1e),
    outline = Color(0xff71787e),
    surfaceVariant = Color(0xffdde3ea),
    onSurfaceVariant = Color(0xff41484d)
)
val DarkColorScheme = darkColorScheme(
    primary = Color(0xff87ceff),
    onPrimary = Color(0xff00344c),
    primaryContainer = Color(0xff004c6d),
    onPrimaryContainer = Color(0xffc8e6ff),
    secondary = Color(0xffb7c9d8),
    onSecondary = Color(0xff21323e),
    secondaryContainer = Color(0xff384956),
    onSecondaryContainer = Color(0xffd2e5f5),
    tertiary = Color(0xffcdc0e9),
    onTertiary = Color(0xff342b4b),
    tertiaryContainer = Color(0xff4b4263),
    onTertiaryContainer = Color(0xffe9ddff),
    error = Color(0xffffb4ab),
    onError = Color(0xff690005),
    errorContainer = Color(0xff93000a),
    onErrorContainer = Color(0xffffdad6),
    background = Color(0xff191c1e),
    onBackground = Color(0xffe2e2e5),
    surface = Color(0xff191c1e),
    onSurface = Color(0xffe2e2e5),
    outline = Color(0xff8b9198),
    surfaceVariant = Color(0xff41484d),
    onSurfaceVariant = Color(0xffc1c7ce)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val dynamicTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        dynamicTheme && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicTheme && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
package com.shlomikatriel.expensesmanager.compose.tooling

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    group = "Standard",
    locale = "en",
    showBackground = true
)
private annotation class StandardComponentPreview

@Preview(
    name = "Light",
    group = "Appearance",
    locale = "en",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark",
    group = "Appearance",
    locale = "en",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
private annotation class AppearanceComponentPreviews

@Preview(
    name = "English",
    group = "Language",
    locale = "en",
    showBackground = true
)
@Preview(
    name = "Hebrew",
    group = "Language",
    locale = "iw",
    showBackground = true
)
private annotation class LanguageComponentPreviews

@StandardComponentPreview
@AppearanceComponentPreviews
@LanguageComponentPreviews
annotation class ComponentPreviews
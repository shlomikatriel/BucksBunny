package com.shlomikatriel.expensesmanager.compose.tooling

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    group = "Standard",
    locale = "en",
    showSystemUi = true
)
private annotation class StandardScreenPreview

@Preview(
    name = "Light",
    group = "Appearance",
    locale = "en",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark",
    group = "Appearance",
    locale = "en",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
private annotation class AppearanceScreenPreviews

@Preview(
    name = "Cell Phone",
    group = "Device",
    locale = "en",
    showSystemUi = true,
    device = Devices.PIXEL_4_XL
)
@Preview(
    name = "Small Tablet",
    group = "Device",
    locale = "en",
    showSystemUi = true,
    device = Devices.NEXUS_7
)
@Preview(
    name = "Large Tablet",
    group = "Device",
    locale = "en",
    showSystemUi = true,
    device = Devices.PIXEL_C
)
private annotation class DeviceScreenPreviews

@Preview(
    name = "English",
    group = "Language",
    locale = "en",
    showSystemUi = true
)
@Preview(
    name = "Hebrew",
    group = "Language",
    locale = "iw",
    showSystemUi = true
)
private annotation class LanguageScreenPreviews

@StandardScreenPreview
@AppearanceScreenPreviews
@DeviceScreenPreviews
@LanguageScreenPreviews
annotation class ScreenPreviews
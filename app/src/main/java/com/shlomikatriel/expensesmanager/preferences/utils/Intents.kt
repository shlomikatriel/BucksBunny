package com.shlomikatriel.expensesmanager.preferences.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.shlomikatriel.expensesmanager.BuildConfig

object Intents {
    fun send(mailAddress: String, subject: String, text: String, fileUri: Uri) = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAddress))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_STREAM, fileUri)
    }

    fun view(url: String) = Intent(Intent.ACTION_VIEW, url.toUri())

    fun appInfo() = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = "package:${BuildConfig.APPLICATION_ID}".toUri()
    }

    fun localeSettings() = Intent(Settings.ACTION_LOCALE_SETTINGS)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun appLocaleSettings() = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
        data = "package:${BuildConfig.APPLICATION_ID}".toUri()
    }
}
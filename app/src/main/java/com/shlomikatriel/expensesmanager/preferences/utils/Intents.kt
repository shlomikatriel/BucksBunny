package com.shlomikatriel.expensesmanager.preferences.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.shlomikatriel.expensesmanager.BuildConfig

object Intents {
    fun send(mailAddress: String, subject: String, text: String, fileUri: Uri) = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAddress))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_STREAM, fileUri)
    }

    fun view(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    fun appInfo() = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
    }
}
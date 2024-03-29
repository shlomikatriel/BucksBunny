package com.shlomikatriel.expensesmanager.preferences.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.logVerbose
import java.util.*

object Locales {
    fun setLocale(locale: Locale?) {
        logInfo(Tag.PREFERENCES, "Setting locale: $locale")
        val localeList = if (locale != null) {
            LocaleListCompat.forLanguageTags(locale.toLanguageTag())
        } else {
            LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun getLocale(): Locale? {
        val localeList = AppCompatDelegate.getApplicationLocales()
        val locale = if (localeList == LocaleListCompat.getEmptyLocaleList()) {
            null
        } else {
            localeList.get(0)
        }
        logInfo(Tag.PREFERENCES, "Getting locale: $locale")
        return locale
    }

    fun getAvailableLocales(): Collection<Locale> {
        val locales = Locale.getAvailableLocales().filter {
            it.isSupported() && it.hasCurrency()
        }
        logVerbose(Tag.PREFERENCES, "Available locales: $locales")
        return locales
    }

    private fun Locale.isSupported(): Boolean {
        return BuildConfig.COUNTRY_CODES.contains(country) && BuildConfig.LANGUAGE_CODES.contains(language) && variant.isNullOrEmpty()
    }

    private fun Locale.hasCurrency() = try {
        Currency.getInstance(this)
        true
    } catch (e: Exception) {
        false
    }

    fun getDisplayText(locale: Locale): String {
        return "${locale.getDisplayLanguage(locale)}, ${locale.getDisplayCountry(locale)} (${Currency.getInstance(locale).getSymbol(locale)})"
    }
}
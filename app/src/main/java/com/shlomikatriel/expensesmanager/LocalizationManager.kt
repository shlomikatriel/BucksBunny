package com.shlomikatriel.expensesmanager

import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.logs.logError
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.StringKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getString
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class LocalizationManager
@Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {

    /**
     * @return all of the locales supported by the app's languages, which have a currency
     * */
    fun getAllSupportedLocales(): List<Locale> {
        logInfo("Fetching all supported locales")
        return Locale.getAvailableLocales()
            .filter {
                BuildConfig.LANGUAGE_CODES.contains(it.language) && hasCurrency(it) && it.displayVariant.isNullOrEmpty()
            }
            .toList()
    }

    private fun hasCurrency(locale: Locale) = try {
        Currency.getInstance(locale)
        true
    } catch (e: Exception) {
        false
    }

    /**
     * If the user chose custom currency, parse locale from locale code (with a bit of magic).
     * Otherwise, use the default locale.
     * */
    private fun getLocaleForCurrency(): Locale {
        val currencyLocaleCode = sharedPreferences.getString(StringKey.CURRENCY)
        val locale = if (currencyLocaleCode.isNullOrEmpty()) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(currencyLocaleCode.replace('_', '-'))
        }
        logInfo("Fetched locale for currency format: $locale")
        return locale
    }

    fun getCurrencySymbol(): String = try {
        Currency.getInstance(getLocaleForCurrency()).symbol
    } catch (e: Exception) {
        logError("Failed to fetch currency symbol", e)
        firebaseCrashlytics.recordException(e)
        Currency.getInstance(Locale.getDefault()).symbol
    }

    fun getCurrencyFormat(): NumberFormat = try {
        DecimalFormat.getCurrencyInstance(getLocaleForCurrency())
    } catch (e: Exception) {
        logError("Failed to fetch currency format", e)
        firebaseCrashlytics.recordException(e)
        DecimalFormat.getCurrencyInstance(Locale.getDefault())
    }

}
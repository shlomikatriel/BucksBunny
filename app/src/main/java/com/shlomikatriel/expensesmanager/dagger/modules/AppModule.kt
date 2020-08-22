package com.shlomikatriel.expensesmanager.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Named

@Module
class AppModule {

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME = "app_preferences"
    }

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    @Provides
    fun provideCurrencyFormat(): NumberFormat = DecimalFormat.getCurrencyInstance()

    @Provides
    @Named("integer")
    fun provideCurrencyIntegerFormat(currencyFormat: NumberFormat): NumberFormat {
        currencyFormat.maximumFractionDigits = 0
        return currencyFormat
    }

    @Provides
    fun provideCurrency(): Currency = Currency.getInstance(Locale.getDefault())
}
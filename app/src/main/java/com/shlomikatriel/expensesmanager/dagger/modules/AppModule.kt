package com.shlomikatriel.expensesmanager.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME = "app_preferences"
    }

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideCurrencyFormat(): NumberFormat = DecimalFormat.getCurrencyInstance()

    @Singleton
    @Provides
    @Named("integer")
    fun provideCurrencyIntegerFormat(currencyFormat: NumberFormat): NumberFormat {
        currencyFormat.maximumFractionDigits = 0
        return currencyFormat
    }

    @Singleton
    @Provides
    fun provideCurrency(): Currency = Currency.getInstance(Locale.getDefault())

    @Singleton
    @Provides
    fun provideFirebaseCrashlytics() = FirebaseCrashlytics.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(context: Context) = FirebaseAnalytics.getInstance(context)
}
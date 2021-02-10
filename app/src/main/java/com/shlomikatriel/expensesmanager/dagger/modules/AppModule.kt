package com.shlomikatriel.expensesmanager.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
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
    fun provideFirebaseCrashlytics() = FirebaseCrashlytics.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(context: Context) = FirebaseAnalytics.getInstance(context)
}
package com.shlomikatriel.expensesmanager.hilt

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME = "app_preferences"
    }

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    @Provides
    fun provideFirebaseCrashlytics() = FirebaseCrashlytics.getInstance()

    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext context: Context) = FirebaseAnalytics.getInstance(context)
}
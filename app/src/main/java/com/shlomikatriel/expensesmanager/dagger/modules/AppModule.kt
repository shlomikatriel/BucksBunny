package com.shlomikatriel.expensesmanager.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME = "app_preferences"
    }

    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
}
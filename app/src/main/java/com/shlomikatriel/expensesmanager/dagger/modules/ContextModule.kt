package com.shlomikatriel.expensesmanager.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val appContext: Context) {

    @Provides
    fun provideAppContext() = appContext
}
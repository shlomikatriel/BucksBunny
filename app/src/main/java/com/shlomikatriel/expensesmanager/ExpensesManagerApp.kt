package com.shlomikatriel.expensesmanager

import android.app.Application
import com.shlomikatriel.expensesmanager.dagger.components.AppComponent
import com.shlomikatriel.expensesmanager.dagger.components.DaggerAppComponent
import com.shlomikatriel.expensesmanager.dagger.modules.ContextModule
import com.shlomikatriel.expensesmanager.logs.Logger

class ExpensesManagerApp: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Logger.i("Creating application")
        createObjectGraph()
        Logger.i("Application created")
    }

    private fun createObjectGraph() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }
}
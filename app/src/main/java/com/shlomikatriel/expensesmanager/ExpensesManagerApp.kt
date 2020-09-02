package com.shlomikatriel.expensesmanager

import android.app.Application
import com.shlomikatriel.expensesmanager.dagger.components.AppComponent
import com.shlomikatriel.expensesmanager.dagger.components.DaggerAppComponent
import com.shlomikatriel.expensesmanager.dagger.modules.ContextModule
import com.shlomikatriel.expensesmanager.logs.LogManager
import com.shlomikatriel.expensesmanager.logs.Logger
import javax.inject.Inject

class ExpensesManagerApp: Application() {

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var logManager: LogManager

    override fun onCreate() {
        super.onCreate()
        createObjectGraph()
        appComponent.inject(this)
        logManager.initializeLogger()
        Logger.i("Application created")
    }

    private fun createObjectGraph() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Logger.i("Low memory")
    }

    override fun onTerminate() {
        super.onTerminate()
        Logger.i("App terminated")
    }
}
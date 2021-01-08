package com.shlomikatriel.expensesmanager

import android.app.Application
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.dagger.components.AppComponent
import com.shlomikatriel.expensesmanager.dagger.components.DaggerAppComponent
import com.shlomikatriel.expensesmanager.dagger.modules.ContextModule
import com.shlomikatriel.expensesmanager.logs.LogManager
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import javax.inject.Inject

class ExpensesManagerApp : Application() {

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        createObjectGraph()
        appComponent.inject(this)
        logManager.initialize()
        logInfo("Creating Application")
        initializeFirebaseServices()
        logInfo("Application created")
    }

    private fun createObjectGraph() {
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }

    private fun initializeFirebaseServices() {
        firebaseAnalytics.setAnalyticsCollectionEnabled(sharedPreferences.getBoolean(BooleanKey.FIREBASE_ANALYTICS_ENABLED))
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(sharedPreferences.getBoolean(BooleanKey.FIREBASE_CRASHLYTICS_ENABLED))
    }

    override fun onLowMemory() {
        super.onLowMemory()
        logInfo("Low memory")
    }
}
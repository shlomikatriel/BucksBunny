package com.shlomikatriel.expensesmanager

import android.app.Application
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.dagger.components.AppComponent
import com.shlomikatriel.expensesmanager.dagger.components.DaggerAppComponent
import com.shlomikatriel.expensesmanager.dagger.modules.ContextModule
import com.shlomikatriel.expensesmanager.logs.LogManager
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.logVerbose
import com.shlomikatriel.expensesmanager.sharedpreferences.*
import javax.inject.Inject

class BucksBunnyApp : Application() {

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

        checkForUpdate()

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

    private fun checkForUpdate() {
        val latestVersionCode = sharedPreferences.getInt(IntKey.LATEST_VERSION_CODE)
        when {
            latestVersionCode == IntKey.LATEST_VERSION_CODE.getDefault() -> {
                logDebug("Initial version: ${BuildConfig.VERSION_NAME}")
                updateVersion()
            }
            BuildConfig.VERSION_CODE > latestVersionCode -> {
                val latestVersionName = sharedPreferences.getString(StringKey.LATEST_VERSION_NAME)
                logDebug("Version update: $latestVersionName -> ${BuildConfig.VERSION_NAME}")
                updateVersion()
            }
            BuildConfig.VERSION_CODE == latestVersionCode -> {
                logVerbose("No update")
            }
        }
    }

    private fun updateVersion() {
        sharedPreferences.putInt(IntKey.LATEST_VERSION_CODE, BuildConfig.VERSION_CODE)
        sharedPreferences.putString(StringKey.LATEST_VERSION_NAME, BuildConfig.VERSION_NAME)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        logInfo("Low memory")
    }
}
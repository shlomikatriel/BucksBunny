package com.shlomikatriel.expensesmanager

import android.app.Application
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.logs.LogManager
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.logVerbose
import com.shlomikatriel.expensesmanager.sharedpreferences.*
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BucksBunnyApp : Application() {

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

        logManager.initialize()

        logInfo("Creating Application")

        initializeFirebaseServices()

        checkForUpdate()

        logInfo("Application created")
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
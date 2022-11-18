package com.shlomikatriel.expensesmanager

import android.app.Application
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.logs.LogManager
import com.shlomikatriel.expensesmanager.logs.dispatching.LogDispatcher
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.logVerbose
import com.shlomikatriel.expensesmanager.logs.loggers.files.FileLogger
import com.shlomikatriel.expensesmanager.logs.loggers.logcat.LogcatLogger
import com.shlomikatriel.expensesmanager.sharedpreferences.*
import dagger.hilt.android.HiltAndroidApp
import java.io.File
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

    @Inject
    lateinit var logcatLogger: LogcatLogger

    @Inject
    lateinit var fileLogger: FileLogger

    override fun onCreate() {
        super.onCreate()

        initializeLoggers()

        logInfo("Creating Application")

        handleUpdate()

        initializeFirebaseServices()

        logInfo("Application created")
    }

    private fun initializeLoggers() {
        if (BuildConfig.DEBUG) {
            LogDispatcher.initializeLoggers(logcatLogger, fileLogger)
        } else {
            LogDispatcher.initializeLoggers(fileLogger)
        }
    }

    private fun initializeFirebaseServices() {
        val anonymousReports = sharedPreferences.getBoolean(BooleanKey.ANONYMOUS_REPORTS_ENABLED)
        firebaseAnalytics.setAnalyticsCollectionEnabled(anonymousReports)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(anonymousReports)
    }

    private fun handleUpdate() {
        val latestVersionCode = sharedPreferences.getInt(IntKey.LATEST_VERSION_CODE)
        when {
            latestVersionCode == IntKey.LATEST_VERSION_CODE.getDefault() -> {
                logDebug("Initial version: ${BuildConfig.VERSION_NAME}")
                updateVersion()
            }
            BuildConfig.VERSION_CODE > latestVersionCode -> {
                val latestVersionName = sharedPreferences.getString(StringKey.LATEST_VERSION_NAME)
                logDebug("Version update: $latestVersionName -> ${BuildConfig.VERSION_NAME}")
                runMigration(latestVersionCode)
                updateVersion()
            }
            BuildConfig.VERSION_CODE == latestVersionCode -> {
                logVerbose("No update")
            }
        }
    }

    private fun runMigration(lastVersionCode: Int) {
        logDebug("Running migrations [lastVersionCode=$lastVersionCode]")
        if (lastVersionCode <= 103005) {
            val analyticsEnabled = sharedPreferences.getBoolean("firebase_analytics_enabled", false)
            val crashlyticsEnabled = sharedPreferences.getBoolean("firebase_crashlytics_enabled", false)
            sharedPreferences.putBoolean(BooleanKey.ANONYMOUS_REPORTS_ENABLED, analyticsEnabled && crashlyticsEnabled)
            sharedPreferences.edit()
                .remove("firebase_analytics_enabled")
                .remove("firebase_crashlytics_enabled")
                .remove("currency")
                .apply()
            File(filesDir, LogManager.LOG_FOLDER_NAME)
                .listFiles { file -> file.name.startsWith("Logs") && file.name.endsWith(".log") }
                ?.forEach { it.delete() }
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
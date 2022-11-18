package com.shlomikatriel.expensesmanager

import android.app.Application
import android.content.SharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.logs.*
import com.shlomikatriel.expensesmanager.logs.dispatching.LogDispatcher
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

        logInfo(Tag.APPLICATION, "Creating Application")

        handleUpdate()

        initializeFirebaseServices()

        logInfo(Tag.APPLICATION, "Application created")
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
                logInfo(Tag.APPLICATION, "Initial version: ${BuildConfig.VERSION_NAME}")
                updateVersion()
            }
            BuildConfig.VERSION_CODE > latestVersionCode -> {
                val latestVersionName = sharedPreferences.getString(StringKey.LATEST_VERSION_NAME)
                logInfo(Tag.APPLICATION, "Version update: $latestVersionName -> ${BuildConfig.VERSION_NAME}")
                runMigration(latestVersionCode)
                updateVersion()
            }
            BuildConfig.VERSION_CODE == latestVersionCode -> {
                logVerbose(Tag.APPLICATION, "No update")
            }
        }
    }

    private fun runMigration(lastVersionCode: Int) {
        logInfo(Tag.APPLICATION, "Running migrations [lastVersionCode=$lastVersionCode]")
        if (lastVersionCode <= 103005) {
            val analyticsEnabled = sharedPreferences.getBoolean("firebase_analytics_enabled", false)
            val crashlyticsEnabled = sharedPreferences.getBoolean("firebase_crashlytics_enabled", false)
            sharedPreferences.putBoolean(BooleanKey.ANONYMOUS_REPORTS_ENABLED, analyticsEnabled && crashlyticsEnabled)
            sharedPreferences.edit()
                .remove("firebase_analytics_enabled")
                .remove("firebase_crashlytics_enabled")
                .remove("currency")
                .apply()
            logInfo(Tag.LOGS, "Deleting old logs")
            File(filesDir, LogManager.LOG_FOLDER_NAME)
                .listFiles { file -> file.name.startsWith("Logs") && file.name.endsWith(".log") }
                ?.forEach {
                    val deleted = it.delete()
                    logDebug(Tag.LOGS, "Old log file ${it.name} deleted: $deleted")
                }
        }
    }

    private fun updateVersion() {
        sharedPreferences.putInt(IntKey.LATEST_VERSION_CODE, BuildConfig.VERSION_CODE)
        sharedPreferences.putString(StringKey.LATEST_VERSION_NAME, BuildConfig.VERSION_NAME)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        logInfo(Tag.APPLICATION, "Low memory")
    }
}
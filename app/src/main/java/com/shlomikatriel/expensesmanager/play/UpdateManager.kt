package com.shlomikatriel.expensesmanager.play

import android.app.Activity
import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logError
import com.shlomikatriel.expensesmanager.logs.logInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {

    suspend fun checkIfUpdateAvailable(
        activity: Activity,
        snackbarHostState: SnackbarHostState
    ) {
        try {
            logInfo(Tag.IN_APP_UPDATE, "Checking if update available")
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            logDebug(Tag.IN_APP_UPDATE, "App update manager created, requesting app update info")
            val appUpdateInfo = appUpdateManager.requestAppUpdateInfo()
            logInfo(Tag.IN_APP_UPDATE, "Checking if update available")

            val availability = appUpdateInfo.updateAvailability()
            logInfo(Tag.IN_APP_UPDATE, "Got result [availability=$availability, availableVersionCode=${appUpdateInfo.availableVersionCode()}]")
            if (availability == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                logInfo(Tag.IN_APP_UPDATE, "Update available, showing app update snackbar")
                val result = snackbarHostState.showSnackbar(
                    message = context.resources.getString(R.string.app_update_instructions),
                    actionLabel = context.resources.getString(R.string.app_update_button),
                    duration = SnackbarDuration.Indefinite,
                    withDismissAction = true
                )
                logInfo(Tag.IN_APP_UPDATE, "Snackbar result: $result")
                when (result) {
                    SnackbarResult.Dismissed -> logInfo(Tag.IN_APP_UPDATE, "User confirmed update snackbar, starting update process")
                    SnackbarResult.ActionPerformed -> {
                        logInfo(Tag.IN_APP_UPDATE, "User confirmed update snackbar, starting update process")
                        startUpdateProcess(appUpdateManager, appUpdateInfo, activity)
                    }
                }
            }
        } catch (t: Throwable) {
            logError(Tag.IN_APP_UPDATE, "Failed to show check up update is available", t)
            firebaseCrashlytics.recordException(t)
        }
    }

    private fun startUpdateProcess(
        appUpdateManager: AppUpdateManager,
        appUpdateInfo: AppUpdateInfo,
        activity: Activity
    ) = try {
        logInfo(Tag.IN_APP_UPDATE, "Starting update process")
        appUpdateManager.startUpdateFlow(appUpdateInfo, activity, AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)).apply {
            addOnSuccessListener {
                logInfo(Tag.IN_APP_UPDATE, "In-app update success")
            }
            addOnFailureListener {
                logError(Tag.IN_APP_UPDATE, "In-app update failure", it)
            }
            addOnCanceledListener {
                logInfo(Tag.IN_APP_UPDATE, "In-app update canceled")
            }
        }
    } catch (e: Exception) {
        logError(Tag.IN_APP_UPDATE, "Failed to start in app update flow", e)
        firebaseCrashlytics.recordException(e)
    }
}
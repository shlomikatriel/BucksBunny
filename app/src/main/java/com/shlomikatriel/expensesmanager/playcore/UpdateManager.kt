package com.shlomikatriel.expensesmanager.playcore

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.startUpdateFlowForResult
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.logs.logError
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.logWarning
import javax.inject.Inject

class UpdateManager
@Inject constructor(private val context: Context) {

    companion object {
        const val UPDATE_REQUEST_CODE = 1000
    }

    private var snackbar: Snackbar? = null

    private fun checkIfUpdateAvailable(
        onUpdateAvailable: (
            appUpdateManager: AppUpdateManager,
            appUpdateInfo: AppUpdateInfo
        ) -> Unit
    ) {
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        logInfo("Checking if update available")

        appUpdateInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val appUpdateInfo = task.result
                val availability = appUpdateInfo.updateAvailability()
                logInfo("Got result [availability=$availability, availableVersionCode=${appUpdateInfo.availableVersionCode()}]")
                if (availability == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    onUpdateAvailable(appUpdateManager, appUpdateInfo)
                }
            } else {
                logWarning("Failed to check for version availability", task.exception)
            }
        }
    }

    fun showUpdateSnackbarIfNeeded(fragment: Fragment, view: View) = try {
        checkIfUpdateAvailable { appUpdateManager, appUpdateInfo ->
            logInfo("Update available, showing app update snackbar")
            snackbar = Snackbar.make(
                view,
                R.string.app_update_instructions,
                Snackbar.LENGTH_LONG
            ).setAction(R.string.app_update_button) {
                startUpdateProcess(appUpdateManager, appUpdateInfo, fragment)
            }
            snackbar?.takeUnless { it.isShown }?.show()
        }
    } catch (e: Exception) {
        logError("Failed to show app update snackbar", e)
    }

    private fun startUpdateProcess(
        appUpdateManager: AppUpdateManager,
        appUpdateInfo: AppUpdateInfo,
        fragment: Fragment
    ) = try {
        logInfo("Starting update process")
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            fragment,
            UPDATE_REQUEST_CODE
        )
    } catch (e: Exception) {
        logError("Failed to start in app update flow", e)
    }

    fun processResult(requestCode: Int, resultCode: Int) {
        if (requestCode == UPDATE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    logInfo("App updated successfully")
                }
                Activity.RESULT_CANCELED -> {
                    logInfo("The user has denied or cancelled the update")
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    logInfo("Failed to update the app, showing snackbar again")
                    snackbar?.takeUnless { it.isShown }?.show()
                }
            }
        }
    }
}
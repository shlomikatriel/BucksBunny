package com.shlomikatriel.expensesmanager.playcore

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import javax.inject.Inject
import kotlin.concurrent.thread

class AppReviewManager
@Inject constructor(private val databaseManager: DatabaseManager) {

    companion object {
        const val EXPENSES_NEEDED_FOR_IN_APP_REVIEW = 8
    }

    private fun isAppReviewNeeded(): Boolean {
        val expensesCount = databaseManager.countExpenses()
        logDebug("Checking if Play In-App Review dialog needed [expensesCount=$expensesCount, expensesNeeded=$EXPENSES_NEEDED_FOR_IN_APP_REVIEW]")
        return expensesCount >= EXPENSES_NEEDED_FOR_IN_APP_REVIEW
    }

    fun showAppReviewDialogIfNeeded(activity: Activity) = thread(name = "AppReview") {
        if (isAppReviewNeeded()) {
            val manager = ReviewManagerFactory.create(activity)
            val reviewFlowRequest = manager.requestReviewFlow()
            logInfo("Requesting In-App Review flow")
            reviewFlowRequest.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    val reviewInfo = request.result
                    logInfo("Starting In-App Review flow")
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        logInfo("In-App Review completed")
                    }
                } else {
                    logDebug("Review flow request unsuccessful")
                }
            }
        }
    }
}
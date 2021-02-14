package com.shlomikatriel.expensesmanager.playcore

import android.app.Activity
import android.content.SharedPreferences
import com.google.android.play.core.review.ReviewManagerFactory
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.LongKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getLong
import com.shlomikatriel.expensesmanager.sharedpreferences.putLong
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.thread

class AppReviewManager
@Inject constructor(
    private val databaseManager: DatabaseManager,
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val EXPENSES_NEEDED_FOR_IN_APP_REVIEW = 8
        const val IN_APP_REVIEW_GRACE_DAYS = 7
    }

    private fun isAppReviewNeeded(): Boolean {
        val expensesCount = databaseManager.countExpenses()
        val lastReview = sharedPreferences.getLong(LongKey.LAST_IN_APP_REVIEW_TIME)
        val currentTime = System.currentTimeMillis()
        val daysSinceLastReview = TimeUnit.MILLISECONDS.toDays(currentTime - lastReview)
        logDebug("Checking if Play In-App Review dialog needed [expensesCount=$expensesCount, daysSinceLastReview=$daysSinceLastReview]")
        return expensesCount >= EXPENSES_NEEDED_FOR_IN_APP_REVIEW && daysSinceLastReview > IN_APP_REVIEW_GRACE_DAYS
    }

    fun showAppReviewDialogIfNeeded(activity: Activity) = thread(name = "AppReview") {
        if (isAppReviewNeeded()) {
            sharedPreferences.putLong(LongKey.LAST_IN_APP_REVIEW_TIME, System.currentTimeMillis())
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
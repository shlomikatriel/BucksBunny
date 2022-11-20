package com.shlomikatriel.expensesmanager.firebase

import android.os.Bundle
import androidx.annotation.Size
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.analytics.ktx.logEvent
import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logWarning

fun FirebaseAnalytics.logEvent(
    @Size(min = 1L, max = 40L) event: String,
    params: Map<String, Any> = emptyMap()
) {
    logDebug(Tag.FIREBASE, "Logging event: $event")
    logEvent(event) {
        params.forEach { param(it.key, it.value) }
    }
}

private fun ParametersBuilder.param(key: String, value: Any) = when (value) {
    is Int -> param(key, value.toLong())
    is Long -> param(key, value)
    is Float -> param(key, value.toDouble())
    is Double -> param(key, value)
    is String -> param(key, value)
    is Bundle -> param(key, value)
    else -> logWarning(Tag.FIREBASE, "Unsupported param type ${value.javaClass.simpleName}")
}
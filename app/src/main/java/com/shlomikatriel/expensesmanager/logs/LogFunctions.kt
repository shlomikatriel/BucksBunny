package com.shlomikatriel.expensesmanager.logs

import android.util.Log
import com.bosphere.filelogger.FL

@Suppress("unused")
inline fun <reified T> T.logVerbose(message: String) = FL.v(message.addCallerPrefix<T>())

@Suppress("unused")
inline fun <reified T> T.logDebug(message: String) = FL.d(message.addCallerPrefix<T>())

@Suppress("unused")
inline fun <reified T> T.logInfo(message: String) = FL.i(message.addCallerPrefix<T>())

@Suppress("unused")
inline fun <reified T> T.logWarning(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        val messageWithStackTrace = "$message\n${Log.getStackTraceString(throwable)}"
        FL.w(messageWithStackTrace.addCallerPrefix<T>())
    } else {
        FL.w(message.addCallerPrefix<T>())
    }
}

@Suppress("unused")
inline fun <reified T> T.logError(message: String, throwable: Throwable? = null) = if (throwable != null) {
    FL.e(message.addCallerPrefix<T>(), throwable)
} else {
    FL.e(message.addCallerPrefix<T>())
}

inline fun <reified T> String.addCallerPrefix() = "[${Thread.currentThread().id}] ${T::class.simpleName}: $this"
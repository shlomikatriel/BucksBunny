package com.shlomikatriel.expensesmanager.logs

import android.util.Log
import com.bosphere.filelogger.FL

fun Any.logVerbose(message: String) = FL.v(message.addCallerPrefix(this))

fun Any.logDebug(message: String) = FL.d(message.addCallerPrefix(this))

fun Any.logInfo(message: String) = FL.i(message.addCallerPrefix(this))

fun Any.logWarning(message: String, throwable: Throwable? = null) = if (throwable != null) {
    FL.w(message.addCallerPrefix(this).appendStackTrace(throwable))
} else {
    FL.w(message.addCallerPrefix(this))
}

fun Any.logError(message: String, throwable: Throwable? = null) = if (throwable != null) {
    FL.e(message.addCallerPrefix(this), throwable)
} else {
    FL.e(message.addCallerPrefix(this))
}

private fun String.addCallerPrefix(caller: Any) = "[${Thread.currentThread().id}] ${caller::class.java.simpleName}: $this"

private fun String.appendStackTrace(throwable: Throwable): String {
    return "$this\n${Log.getStackTraceString(throwable)}"
}
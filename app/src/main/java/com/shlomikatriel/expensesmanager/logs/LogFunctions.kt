package com.shlomikatriel.expensesmanager.logs

import android.util.Log
import com.bosphere.filelogger.FL
import com.shlomikatriel.expensesmanager.logs.dispatching.LogDispatcher

@Suppress("unused")
inline fun <reified T> T.logVerbose(message: String) = FL.v(message.addCallerPrefix<T>())

fun logVerbose(tag: Tag, message: String, t: Throwable? = null) = LogDispatcher.verbose(tag, message.addPrefix(), t)

@Suppress("unused")
inline fun <reified T> T.logDebug(message: String) = FL.d(message.addCallerPrefix<T>())

fun logDebug(tag: Tag, message: String, t: Throwable? = null) = LogDispatcher.debug(tag, message.addPrefix(), t)

@Suppress("unused")
inline fun <reified T> T.logInfo(message: String) = FL.i(message.addCallerPrefix<T>())

fun logInfo(tag: Tag, message: String, t: Throwable? = null) = LogDispatcher.info(tag, message.addPrefix(), t)

@Suppress("unused")
inline fun <reified T> T.logWarning(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        val messageWithStackTrace = "$message\n${Log.getStackTraceString(throwable)}"
        FL.w(messageWithStackTrace.addCallerPrefix<T>())
    } else {
        FL.w(message.addCallerPrefix<T>())
    }
}

fun logWarning(tag: Tag, message: String, t: Throwable? = null) = LogDispatcher.warning(tag, message.addPrefix(), t)

@Suppress("unused")
inline fun <reified T> T.logError(message: String, throwable: Throwable? = null) = if (throwable != null) {
    FL.e(message.addCallerPrefix<T>(), throwable)
} else {
    FL.e(message.addCallerPrefix<T>())
}

fun logError(tag: Tag, message: String, t: Throwable? = null) = LogDispatcher.error(tag, message.addPrefix(), t)

inline fun <reified T> String.addCallerPrefix() = "[${Thread.currentThread().id}] ${T::class.simpleName}: $this"

private fun String.addPrefix() = "[${android.os.Process.myPid()}${Thread.currentThread().id}] $this"
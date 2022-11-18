package com.shlomikatriel.expensesmanager.logs

import com.shlomikatriel.expensesmanager.logs.dispatching.LogDispatcher

fun logVerbose(tag: Tag, message: String, t: Throwable? = null) {
    LogDispatcher.verbose(tag, message.addPrefix(), t)
}

fun logDebug(tag: Tag, message: String, t: Throwable? = null) {
    LogDispatcher.debug(tag, message.addPrefix(), t)
}

fun logInfo(tag: Tag, message: String, t: Throwable? = null) {
    LogDispatcher.info(tag, message.addPrefix(), t)
}

fun logWarning(tag: Tag, message: String, t: Throwable? = null) {
    LogDispatcher.warning(tag, message.addPrefix(), t)
}

fun logError(tag: Tag, message: String, t: Throwable? = null) {
    LogDispatcher.error(tag, message.addPrefix(), t)
}

private fun String.addPrefix() = "[${android.os.Process.myPid()}${Thread.currentThread().id}] $this"
package com.shlomikatriel.expensesmanager.logs

import android.util.Log
import com.shlomikatriel.expensesmanager.BuildConfig

@Suppress("unused")
class Logger {
    companion object {
        fun e(message: String, throwable: Throwable? = null) = if (throwable != null) {
            Log.e(BuildConfig.LOG_TAG, appendMessageToCallerDetails(message), throwable)
        } else {
            Log.e(BuildConfig.LOG_TAG, appendMessageToCallerDetails(message))
        }

        fun w(message: String, throwable: Throwable? = null) = if (throwable != null) {
            Log.w(BuildConfig.LOG_TAG, appendMessageToCallerDetails(message), throwable)
        } else {
            Log.w(BuildConfig.LOG_TAG, appendMessageToCallerDetails(message))
        }

        fun i(message: String) = Log.i(BuildConfig.LOG_TAG, appendMessageToCallerDetails(message))

        fun d(message: String) = Log.d(BuildConfig.LOG_TAG, appendMessageToCallerDetails(message))

        fun v(message: String) = Log.v(BuildConfig.LOG_TAG, appendMessageToCallerDetails(message))

        private fun appendMessageToCallerDetails(message: String): String {
            val thread = Thread.currentThread()
            return thread.stackTrace[4].let {
                val classSimpleName = simplifyFullyQualifiedClassName(it.className)
                "[${thread.id}] $classSimpleName#${it.methodName}: $message"
            }
        }

        private fun simplifyFullyQualifiedClassName(className: String) = className.substringAfterLast('.')
    }
}
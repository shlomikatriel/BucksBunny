package com.shlomikatriel.expensesmanager.logs

import android.util.Log
import com.bosphere.filelogger.FL


class Logger {
    companion object {
        fun v(message: String) = FL.v(message.addCallerPrefix())

        fun d(message: String) = FL.d(message.addCallerPrefix())

        fun i(message: String) = FL.i(message.addCallerPrefix())

        fun w(message: String, throwable: Throwable? = null) = if (throwable != null) {
            FL.w(message.addCallerPrefix().appendStackTrace(throwable))
        } else {
            FL.w(message.addCallerPrefix())
        }

        fun e(message: String, throwable: Throwable? = null) = if (throwable != null) {
            FL.e(message.addCallerPrefix(), throwable)
        } else {
            FL.e(message.addCallerPrefix())
        }

        private fun String.addCallerPrefix(): String {
            val thread = Thread.currentThread()
            return thread.stackTrace[4].let {
                val classSimpleName = simplifyFullyQualifiedClassName(it.className)
                "[${thread.id}] $classSimpleName#${it.methodName}: $this"
            }
        }

        private fun String.appendStackTrace(throwable: Throwable): String {
            return "$this\n${Log.getStackTraceString(throwable)}"
        }

        private fun simplifyFullyQualifiedClassName(className: String): String {
            return className.substringAfterLast('.')
        }
    }
}
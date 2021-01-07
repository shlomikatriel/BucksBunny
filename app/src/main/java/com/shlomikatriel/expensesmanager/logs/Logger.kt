package com.shlomikatriel.expensesmanager.logs

import android.util.Log
import com.bosphere.filelogger.FL


class Logger {
    companion object {
        fun v(message: String) = FL.v(message.addThreadId())

        fun d(message: String) = FL.d(message.addThreadId())

        fun i(message: String) = FL.i(message.addThreadId())

        fun w(message: String, throwable: Throwable? = null) = if (throwable != null) {
            FL.w(message.addThreadId().appendStackTrace(throwable))
        } else {
            FL.w(message.addThreadId())
        }

        fun e(message: String, throwable: Throwable? = null) = if (throwable != null) {
            FL.e(message.addThreadId(), throwable)
        } else {
            FL.e(message.addThreadId())
        }

        private fun String.addThreadId() = "[${Thread.currentThread().id}] $this"

        private fun String.appendStackTrace(throwable: Throwable): String {
            return "$this\n${Log.getStackTraceString(throwable)}"
        }
    }
}
package com.shlomikatriel.expensesmanager.logs.loggers.logcat

import android.util.Log
import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.loggers.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogcatLogger @Inject constructor(): Logger() {

    override fun verbose(tag: Tag, message: String, t: Throwable?) {
        if (t != null) {
            Log.v(tag.tag, message, t)
        } else {
            Log.v(tag.tag, message)
        }
    }

    override fun debug(tag: Tag, message: String, t: Throwable?) {
        if (t != null) {
            Log.d(tag.tag, message, t)
        } else {
            Log.d(tag.tag, message)
        }
    }

    override fun info(tag: Tag, message: String, t: Throwable?) {
        if (t != null) {
            Log.i(tag.tag, message, t)
        } else {
            Log.i(tag.tag, message)
        }
    }

    override fun warning(tag: Tag, message: String, t: Throwable?) {
        if (t != null) {
            Log.w(tag.tag, message, t)
        } else {
            Log.w(tag.tag, message)
        }
    }

    override fun error(tag: Tag, message: String, t: Throwable?) {
        if (t != null) {
            Log.e(tag.tag, message, t)
        } else {
            Log.e(tag.tag, message)
        }
    }
}
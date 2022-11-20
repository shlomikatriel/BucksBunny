package com.shlomikatriel.expensesmanager.logs.dispatching

import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.loggers.Logger

object LogDispatcher: Logger() {
    private val loggers = mutableSetOf<Logger>()

    // needed to prevent concurrent modification
    private var initialized = false

    fun initializeLoggers(vararg loggers: Logger) {
        this.loggers.addAll(loggers)
        initialized = true
    }

    override fun verbose(tag: Tag, message: String, t: Throwable?) {
        if (!initialized) return

        loggers.forEach {
            it.verbose(tag, message, t)
        }
    }

    override fun debug(tag: Tag, message: String, t: Throwable?) {
        if (!initialized) return

        loggers.forEach {
            it.debug(tag, message, t)
        }
    }

    override fun info(tag: Tag, message: String, t: Throwable?) {
        if (!initialized) return

        loggers.forEach {
            it.info(tag, message, t)
        }
    }

    override fun warning(tag: Tag, message: String, t: Throwable?) {
        if (!initialized) return

        loggers.forEach {
            it.warning(tag, message, t)
        }
    }

    override fun error(tag: Tag, message: String, t: Throwable?) {
        if (!initialized) return

        loggers.forEach {
            it.error(tag, message, t)
        }
    }
}
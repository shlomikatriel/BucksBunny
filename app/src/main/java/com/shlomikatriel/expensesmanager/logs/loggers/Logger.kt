package com.shlomikatriel.expensesmanager.logs.loggers

import com.shlomikatriel.expensesmanager.logs.Tag

abstract class Logger {
    open fun verbose(tag: Tag, message: String, t: Throwable?) {}
    open fun debug(tag: Tag, message: String, t: Throwable?) {}
    open fun info(tag: Tag, message: String, t: Throwable?) {}
    open fun warning(tag: Tag, message: String, t: Throwable?) {}
    open fun error(tag: Tag, message: String, t: Throwable?) {}
}
package com.shlomikatriel.expensesmanager.logs.loggers.files

import com.shlomikatriel.expensesmanager.logs.Tag

data class LogMessage(val timeStamp: Long, val tag: Tag, val message: String, val logLevel: Char)

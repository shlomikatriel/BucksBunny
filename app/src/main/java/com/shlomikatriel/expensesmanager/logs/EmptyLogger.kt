package com.shlomikatriel.expensesmanager.logs

import com.bosphere.filelogger.Loggable
import javax.inject.Inject

class EmptyLogger
@Inject constructor() : Loggable {
    override fun v(tag: String?, log: String?) {}

    override fun d(tag: String?, log: String?) {}

    override fun i(tag: String?, log: String?) {}

    override fun w(tag: String?, log: String?) {}

    override fun e(tag: String?, log: String?) {}

    override fun e(tag: String?, log: String?, tr: Throwable?) {}
}
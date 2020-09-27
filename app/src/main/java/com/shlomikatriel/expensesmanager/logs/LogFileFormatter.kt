package com.shlomikatriel.expensesmanager.logs

import com.bosphere.filelogger.FLConfig
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LogFileFormatter
@Inject constructor() : FLConfig.DefaultFormatter() {

    override fun formatFileName(timeInMillis: Long): String {
        val date = SimpleDateFormat("yyyy.MM.dd", Locale.ROOT)
            .format(Date(timeInMillis))
        return "Logs-$date.log"
    }
}
package com.shlomikatriel.expensesmanager

import com.shlomikatriel.expensesmanager.logs.logDebug
import java.util.*
import javax.inject.Inject

class Utils
@Inject constructor() {

    /**
     * @return the months that passed since year 0
     * */
    fun getMonthOfPosition(position: Int): Int {
        val offset = position - BuildConfig.MAX_MONTHS_OFFSET
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, offset)
        val monthInYear = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val month = 12 * year + monthInYear
        logDebug("Offset $offset transformed to month [monthInYear=$monthInYear, year=$year, month=$month]")
        return month
    }
}
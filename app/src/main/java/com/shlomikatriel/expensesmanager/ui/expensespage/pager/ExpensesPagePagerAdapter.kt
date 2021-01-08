package com.shlomikatriel.expensesmanager.ui.expensespage.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.ui.expensespage.fragments.ExpensesPageFragment
import com.shlomikatriel.expensesmanager.ui.expensespage.fragments.ExpensesPageFragmentArgs
import java.util.*

class ExpensesPagePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = arrayOfNulls<Fragment>(BuildConfig.MONTHS_COUNT)

    override fun getItemCount() = BuildConfig.MONTHS_COUNT

    @Synchronized
    override fun createFragment(position: Int) = fragments[position] ?: createFragmentAndUpdateCache(position)

    private fun createFragmentAndUpdateCache(position: Int): Fragment {
        logInfo("Creating new fragment for page $position")
        val fragment = ExpensesPageFragment().apply {
            val month = getMonthOfPosition(position)
            arguments = ExpensesPageFragmentArgs(position, month).toBundle()
        }
        fragments[position] = fragment
        return fragment
    }

    /**
     * @return the months that passed since year 0
     * */
    private fun getMonthOfPosition(position: Int): Int {
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
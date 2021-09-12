package com.shlomikatriel.expensesmanager.expenses.components

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.Utils
import com.shlomikatriel.expensesmanager.expenses.fragments.ExpensesPageFragment
import com.shlomikatriel.expensesmanager.expenses.fragments.ExpensesPageFragmentArgs
import com.shlomikatriel.expensesmanager.logs.logInfo

class ExpensesPagePagerAdapter(private val utils: Utils, fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = arrayOfNulls<Fragment>(BuildConfig.MONTHS_COUNT)

    override fun getItemCount() = BuildConfig.MONTHS_COUNT

    @Synchronized
    override fun createFragment(position: Int) = fragments[position] ?: createFragmentAndUpdateCache(position)

    private fun createFragmentAndUpdateCache(position: Int): Fragment {
        logInfo("Creating new fragment for page $position")
        val fragment = ExpensesPageFragment().apply {
            val month = utils.getMonthOfPosition(position)
            arguments = ExpensesPageFragmentArgs(month).toBundle()
        }
        fragments[position] = fragment
        return fragment
    }
}
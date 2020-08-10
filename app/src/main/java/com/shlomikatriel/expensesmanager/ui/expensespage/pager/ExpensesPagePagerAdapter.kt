package com.shlomikatriel.expensesmanager.ui.expensespage.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shlomikatriel.expensesmanager.BuildConfig
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.ui.expensespage.fragments.ExpensesPageFragment
import com.shlomikatriel.expensesmanager.ui.expensespage.fragments.ExpensesPageFragmentArgs
import java.util.*

class ExpensesPagePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = arrayOfNulls<Fragment>(BuildConfig.MONTHS_COUNT)

    override fun getItemCount() = BuildConfig.MONTHS_COUNT

    @Synchronized
    override fun createFragment(position: Int) = fragments[position] ?: createFragmentAndUpdateCache(position)

    private fun createFragmentAndUpdateCache(position: Int): Fragment {
        Logger.i("Creating new fragment for page $position")
        val fragment = ExpensesPageFragment().apply {
            val (month, year) = transformPositionToMonthAndYear(position)
            arguments = ExpensesPageFragmentArgs(position, month, year).toBundle()
        }
        fragments[position] = fragment
        return fragment
    }

    private fun transformPositionToMonthAndYear(position: Int): Pair<Int, Int> {
        val offset = position - BuildConfig.MAX_MONTHS_OFFSET
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, offset)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        Logger.v("Offset $offset transformed to (month, year)=($month, $year)")
        return Pair(month, year)
    }
}
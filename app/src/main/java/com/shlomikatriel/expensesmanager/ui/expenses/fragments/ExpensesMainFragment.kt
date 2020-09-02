package com.shlomikatriel.expensesmanager.ui.expenses.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.ExpensesMainFragmentBinding
import com.shlomikatriel.expensesmanager.extensions.navigate
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.putBoolean
import com.shlomikatriel.expensesmanager.ui.configureToolbar
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections.Companion.openChooseIncomeDialog
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections.Companion.openSettingsFragment
import com.shlomikatriel.expensesmanager.ui.expenses.mvi.ExpensesEvent
import com.shlomikatriel.expensesmanager.ui.expenses.mvi.ExpensesViewModel
import com.shlomikatriel.expensesmanager.ui.expenses.mvi.ExpensesViewState
import com.shlomikatriel.expensesmanager.ui.expensespage.pager.ExpensesPagePagerAdapter
import com.shlomikatriel.expensesmanager.ui.startPopAnimation
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExpensesMainFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    private lateinit var binding: ExpensesMainFragmentBinding

    val model: ExpensesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate<ExpensesMainFragmentBinding>(
            inflater,
            R.layout.expenses_main_fragment,
            container,
            false
        ).apply {
            fragment = this@ExpensesMainFragment
        }

        configureViewPager()

        model.postEvent(ExpensesEvent.InitializeEvent)

        model.getViewState().observe(viewLifecycleOwner, { render(it) })

        if (!sharedPreferences.getBoolean(BooleanKey.CHOOSE_INCOME_DIALOG_SHOWN)) {
            sharedPreferences.putBoolean(BooleanKey.CHOOSE_INCOME_DIALOG_SHOWN, true)
            navigate(openChooseIncomeDialog(fromOnBoarding = true))
        }

        configureToolbar(R.string.app_name)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.expenses_main_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem) = if (item.itemId == R.id.settings) {
        navigate(openSettingsFragment())
        true
    } else {
        false
    }

    private fun configureViewPager() = binding.pager.apply {
        adapter = ExpensesPagePagerAdapter(this@ExpensesMainFragment)
        TabLayoutMediator(binding.dots, binding.pager) { _, position ->
            binding.pager.setCurrentItem(position, false)
        }.attach()

        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Logger.d("Page $position selected")
                model.postEvent(ExpensesEvent.MonthChangeEvent(position))
            }
        })
    }

    fun onPreviousMonthClicked(view: View?) {
        Logger.d("Previous month clicked")
        view?.startPopAnimation()
        model.postEvent(ExpensesEvent.MonthChangeEvent(binding.pager.currentItem - 1))
    }

    fun onNextMonthClicked(view: View?) {
        Logger.d("Next month clicked")
        view?.startPopAnimation()
        model.postEvent(ExpensesEvent.MonthChangeEvent(binding.pager.currentItem + 1))
    }

    private fun render(viewState: ExpensesViewState) {
        viewState.time?.let { time ->
            binding.date.text = dateFormat.format(Date(time))
        }
        viewState.selectedPage?.let { selectedPage ->
            if (binding.pager.currentItem != selectedPage) binding.pager.setCurrentItem(
                selectedPage,
                true
            )
        }
    }
}
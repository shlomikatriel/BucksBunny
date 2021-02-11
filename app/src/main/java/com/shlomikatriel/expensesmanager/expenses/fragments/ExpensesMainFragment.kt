package com.shlomikatriel.expensesmanager.expenses.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.shlomikatriel.expensesmanager.BucksBunnyApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.Utils
import com.shlomikatriel.expensesmanager.configureToolbar
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.databinding.ExpensesMainFragmentBinding
import com.shlomikatriel.expensesmanager.expenses.components.ExpensesPagePagerAdapter
import com.shlomikatriel.expensesmanager.expenses.fragments.ExpensesMainFragmentDirections.Companion.openAddExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.fragments.ExpensesMainFragmentDirections.Companion.openOnboardingFragment
import com.shlomikatriel.expensesmanager.expenses.fragments.ExpensesMainFragmentDirections.Companion.openSettingsFragment
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesMainEvent
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesMainViewModel
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesMainViewState
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.logVerbose
import com.shlomikatriel.expensesmanager.navigation.navigate
import com.shlomikatriel.expensesmanager.playcore.AppReviewManager
import com.shlomikatriel.expensesmanager.playcore.UpdateManager
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.putBoolean
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExpensesMainFragment : Fragment() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var appReviewManager: AppReviewManager

    @Inject
    lateinit var updateManager: UpdateManager

    @Inject
    lateinit var utils: Utils

    @Suppress("SpellCheckingInspection")
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    private lateinit var binding: ExpensesMainFragmentBinding

    private val model: ExpensesMainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireContext().applicationContext as BucksBunnyApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate<ExpensesMainFragmentBinding>(
            inflater,
            R.layout.expenses_main_fragment,
            container,
            false
        ).apply {
            fragment = this@ExpensesMainFragment
        }

        configureViewPager()

        model.apply {
            postEvent(ExpensesMainEvent.Initialize)
            getViewState().observe(viewLifecycleOwner, { render(it) })
        }

        configureToolbar(R.string.app_name)

        val onboardingShown = showOnboardingIfNeeded()
        if (!onboardingShown) {
            activity?.let {
                appReviewManager.showAppReviewDialogIfNeeded(it)
            }
            updateManager.showUpdateSnackbarIfNeeded(this, binding.root)
        }

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
        adapter = ExpensesPagePagerAdapter(utils, this@ExpensesMainFragment)

        TabLayoutMediator(binding.dots, binding.pager) { _, _ -> }.attach()

        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                logDebug("Page $position selected")
                model.postEvent(ExpensesMainEvent.MonthChange(position))
            }
        })

        startHintAnimation()
    }

    private fun render(viewState: ExpensesMainViewState) {
        logVerbose("Rendering: $viewState")
        viewState.time?.let { time ->
            binding.date.text = dateFormat.format(Date(time))
        }
        viewState.forceSelectPage?.let { selectedPage ->
            if (binding.pager.currentItem != selectedPage) {
                binding.pager.setCurrentItem(selectedPage, false)
            }
        }
        if (viewState.income != null && viewState.expenses != null) {
            binding.expensesGraph.updateGraph(viewState.income, viewState.expenses)
        }
    }

    private fun startHintAnimation() {
        val listener = object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.pager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val pixels = binding.pager.width / 8
                ValueAnimator.ofInt(0, pixels).apply {
                    duration = 200L
                    interpolator = DecelerateInterpolator()
                    repeatCount = 3
                    repeatMode = ValueAnimator.REVERSE
                    addUpdateListener {
                        binding.pager.scrollX = it.animatedValue as Int
                    }
                }.start()
            }
        }
        binding.pager.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    /**
     * @return if onboarding was shown
     * */
    private fun showOnboardingIfNeeded() = if (sharedPreferences.getBoolean(BooleanKey.SHOULD_SHOW_ONBOARDING)) {
        sharedPreferences.putBoolean(BooleanKey.SHOULD_SHOW_ONBOARDING, false)
        navigate(openOnboardingFragment())
        true
    } else {
        false
    }

    fun addExpenseClicked(expenseType: ExpenseType) {
        val month = utils.getMonthOfPosition(binding.pager.currentItem)
        logInfo("Add expense button clicked [expenseType=$expenseType, month=$month]")
        navigate(openAddExpenseDialog(expenseType, month))
        binding.motionLayout.post {
            // navigation animation + collapse animation impact graphics
            // to optimize, schedule collapse animation to start on the next main looper iteration
            binding.motionLayout.transitionToState(R.id.collapsed)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateManager.processResult(requestCode, resultCode)
    }
}
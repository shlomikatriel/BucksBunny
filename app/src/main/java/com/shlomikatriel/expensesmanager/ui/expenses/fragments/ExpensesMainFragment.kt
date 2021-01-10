package com.shlomikatriel.expensesmanager.ui.expenses.fragments

import android.animation.ValueAnimator
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
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.playcore.AppReviewManager
import com.shlomikatriel.expensesmanager.databinding.ExpensesMainFragmentBinding
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.navigation.navigate
import com.shlomikatriel.expensesmanager.playcore.UpdateManager
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.putBoolean
import com.shlomikatriel.expensesmanager.ui.configureToolbar
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections.Companion.openOnboardingFragment
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections.Companion.openSettingsFragment
import com.shlomikatriel.expensesmanager.ui.expenses.mvi.ExpensesEvent
import com.shlomikatriel.expensesmanager.ui.expenses.mvi.ExpensesViewModel
import com.shlomikatriel.expensesmanager.ui.expenses.mvi.ExpensesViewState
import com.shlomikatriel.expensesmanager.ui.expensespage.pager.ExpensesPagePagerAdapter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExpensesMainFragment : Fragment() {


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var appReviewManager: AppReviewManager

    @Inject
    lateinit var updateManager: UpdateManager

    @Suppress("SpellCheckingInspection")
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    private lateinit var binding: ExpensesMainFragmentBinding

    val model: ExpensesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        adapter = ExpensesPagePagerAdapter(this@ExpensesMainFragment)

        TabLayoutMediator(binding.dots, binding.pager) { _, _ -> }.attach()

        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                logDebug("Page $position selected")
                model.postEvent(ExpensesEvent.MonthChangeEvent(position))
            }
        })

        startHintAnimation()
    }

    private fun render(viewState: ExpensesViewState) {
        viewState.time?.let { time ->
            binding.date.text = dateFormat.format(Date(time))
        }
        viewState.forceSelectPage?.let { selectedPage ->
            if (binding.pager.currentItem != selectedPage) binding.pager.setCurrentItem(
                selectedPage,
                false
            )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateManager.processResult(requestCode, resultCode)
    }
}
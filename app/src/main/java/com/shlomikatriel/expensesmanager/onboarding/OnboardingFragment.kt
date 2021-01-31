package com.shlomikatriel.expensesmanager.onboarding

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.OnboardingFragmentBinding
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.hideToolbar
import javax.inject.Inject

class OnboardingFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: OnboardingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate<OnboardingFragmentBinding>(
            inflater,
            R.layout.onboarding_fragment,
            container,
            false
        ).apply {
            fragment = this@OnboardingFragment
        }

        hideToolbar()

        createPagerAdapter()

        return binding.root
    }

    fun nextClicked() {
        val currentPosition = binding.pager.currentItem
        val isLast = currentPosition == OnboardingStage.values().size - 1
        logInfo("User clicked Next [currentPosition=$currentPosition, isLast=$isLast]")
        if (isLast) {
            findNavController().popBackStack()
        } else {
            binding.pager.currentItem = currentPosition + 1
        }
    }

    private fun createPagerAdapter() {
        binding.pager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount() = OnboardingStage.values().size

            override fun createFragment(position: Int) =
                OnboardingStage.values()[position].createFragment()

        }

        TabLayoutMediator(binding.dots, binding.pager) { _, _ -> }.attach()

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                logDebug("Onboarding stage $position selected")
                val isLast = position == OnboardingStage.values().size - 1
                binding.next.setText(if (isLast) R.string.onboarding_lets_get_started else R.string.onboarding_next)
            }
        })
    }
}
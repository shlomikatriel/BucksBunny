package com.shlomikatriel.expensesmanager.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.OnboardingWelcomeFragmentBinding

class OnboardingWelcomeFragment : Fragment() {

    lateinit var binding: OnboardingWelcomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.onboarding_welcome_fragment,
            container,
            false
        )

        startPopAnimation()

        return binding.root
    }

    private fun startPopAnimation() {
        val animation = ScaleAnimation(
            1.0f,
            1.2f,
            1.0f,
            1.2f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
            duration = 800L
        }
        binding.expensesSlogen.startAnimation(animation)
    }
}
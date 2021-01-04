package com.shlomikatriel.expensesmanager.onboarding

import androidx.fragment.app.Fragment

enum class OnboardingStage {
    WELCOME {
        override fun createFragment() = OnboardingWelcomeFragment()
    },
    INCOME {
        override fun createFragment() = OnboardingIncomeFragment()
    },
    ANONYMOUS_DATA {
        override fun createFragment() = OnboardingAnonymousDataFragment()
    };

    abstract fun createFragment(): Fragment
}
package com.shlomikatriel.expensesmanager.onboarding

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shlomikatriel.expensesmanager.BucksBunnyApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.OnboardingAnonymousDataFragmentBinding
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.putBoolean
import javax.inject.Inject

class OnboardingAnonymousDataFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    lateinit var binding: OnboardingAnonymousDataFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireContext().applicationContext as BucksBunnyApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.onboarding_anonymous_data_fragment,
            container,
            false
        )

        configurePermissions()

        return binding.root
    }

    private fun configurePermissions() {
        binding.crashReports.permissionSwitch.apply {
            isChecked = sharedPreferences.getBoolean(BooleanKey.FIREBASE_CRASHLYTICS_ENABLED)
            setOnCheckedChangeListener { _, isChecked ->
                sharedPreferences.putBoolean(BooleanKey.FIREBASE_CRASHLYTICS_ENABLED, isChecked)
                firebaseCrashlytics.setCrashlyticsCollectionEnabled(isChecked)
            }
        }
        binding.usageDataReports.permissionSwitch.apply {
            isChecked = sharedPreferences.getBoolean(BooleanKey.FIREBASE_ANALYTICS_ENABLED)
            setOnCheckedChangeListener { _, isChecked ->
                sharedPreferences.putBoolean(BooleanKey.FIREBASE_ANALYTICS_ENABLED, isChecked)
                firebaseAnalytics.setAnalyticsCollectionEnabled(isChecked)
            }
        }
    }
}
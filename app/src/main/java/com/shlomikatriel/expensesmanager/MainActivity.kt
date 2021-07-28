package com.shlomikatriel.expensesmanager

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.shlomikatriel.expensesmanager.databinding.MainActivityBinding
import com.shlomikatriel.expensesmanager.firebase.logEvent
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.navigation.findNavController
import com.shlomikatriel.expensesmanager.sharedpreferences.IntKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getInt
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as BucksBunnyApp).appComponent.inject(this)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.main_activity
        )

        setSupportActionBar(binding.appBar)

        attachDestinationChangedListener()
        toggleOrientationLock()
        configureDarkMode()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun toggleOrientationLock() {
        if (!resources.getBoolean(R.bool.is_tablet)) {
            logDebug("Locking orientation")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun attachDestinationChangedListener() = findNavController()
        .addOnDestinationChangedListener { _, destination, _ ->
            logInfo("User navigated to ${destination.label}")
            firebaseAnalytics.logEvent("${destination.label}_opened")
        }

    private fun configureDarkMode() {
        val mode = sharedPreferences.getInt(IntKey.DARK_MODE)
        logInfo("Dark mode: $mode")
        if (mode != AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    fun configureToolbar(
        @StringRes title: Int,
        navigateUpEnabled: Boolean
    ) {
        binding.appBar.visibility = View.VISIBLE
        supportActionBar!!.title = getString(title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(navigateUpEnabled)
        if (navigateUpEnabled) {
            binding.appBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        } else {
            binding.appBar.setNavigationOnClickListener(null)
        }
    }

    fun hideToolbar() {
        binding.appBar.visibility = View.GONE
    }
}
package com.shlomikatriel.expensesmanager.ui

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.MainActivityBinding
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.sharedpreferences.IntKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getInt
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as ExpensesManagerApp).appComponent.inject(this)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.main_activity
        )

        setSupportActionBar(binding.appBar)

        attachDestinationChangedListener()

        applyConfigurations()
    }

    private fun applyConfigurations() {
        toggleOrientationLock()
        configureDarkMode()
    }

    private fun toggleOrientationLock() {
        if (!resources.getBoolean(R.bool.is_tablet)) {
            Logger.d("Locking orientation")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun attachDestinationChangedListener() = findNavController(R.id.nav_host_fragment)
        .addOnDestinationChangedListener { _, destination, arguments ->
            Logger.i("User navigated to ${destination.label} with arguments: $arguments")
        }

    private fun configureDarkMode() {
        val mode = sharedPreferences.getInt(IntKey.DARK_MODE)
        Logger.i("Dark mode: $mode")
        if (mode != AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    fun configureToolbar(
        @StringRes title: Int,
        navigateUpEnabled: Boolean
    ) {
        supportActionBar!!.title = getString(title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(navigateUpEnabled)
        if (navigateUpEnabled) {
            binding.appBar.setNavigationOnClickListener {
                findNavController(R.id.nav_host_fragment).popBackStack()
            }
        } else {
            binding.appBar.setNavigationOnClickListener(null)
        }
    }
}
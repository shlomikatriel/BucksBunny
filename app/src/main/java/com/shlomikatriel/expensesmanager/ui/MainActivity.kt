package com.shlomikatriel.expensesmanager.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.MainActivityBinding
import com.shlomikatriel.expensesmanager.logs.Logger

class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.main_activity
        )

        attachDestinationChangedListener()
    }

    private fun attachDestinationChangedListener() = findNavController(R.id.nav_host_fragment)
        .addOnDestinationChangedListener { _, destination, arguments ->
            Logger.i("User navigated to ${destination.label} with arguments: $arguments")
        }
}
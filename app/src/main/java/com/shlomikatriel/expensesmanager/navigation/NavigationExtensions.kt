package com.shlomikatriel.expensesmanager.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.logs.logWarning

fun Fragment.navigate(navDirections: NavDirections) = try {
    findNavController().navigate(navDirections)
} catch (e: Exception) {
    logWarning("Failed to navigate to destination ${e.message}")
}

fun AppCompatActivity.findNavController(): NavController {
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    return navHostFragment.navController
}
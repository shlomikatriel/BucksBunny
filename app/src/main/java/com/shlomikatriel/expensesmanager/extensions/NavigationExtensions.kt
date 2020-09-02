package com.shlomikatriel.expensesmanager.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.shlomikatriel.expensesmanager.logs.Logger

fun Fragment.navigate(navDirections: NavDirections)  = try {
    findNavController().navigate(navDirections)
} catch (e: Exception) {
    Logger.w("Failed to navigate to destination ${e.message}")
}
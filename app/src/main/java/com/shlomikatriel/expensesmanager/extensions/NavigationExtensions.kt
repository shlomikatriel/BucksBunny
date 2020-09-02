package com.shlomikatriel.expensesmanager.extensions

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.shlomikatriel.expensesmanager.logs.Logger
import java.lang.Exception

fun NavController.safeNavigate(navDirections: NavDirections) = try {
    navigate(navDirections)
} catch (e: Exception) {
    Logger.w("Failed to navigate to destination ${e.message}")
}
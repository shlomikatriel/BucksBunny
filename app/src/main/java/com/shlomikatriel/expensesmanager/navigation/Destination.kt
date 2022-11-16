package com.shlomikatriel.expensesmanager.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.shlomikatriel.expensesmanager.R

data class TopBarDetails(@StringRes val title: Int, val hasBackNavigation: Boolean = true, val actions: List<Action>? = null) {
    data class Action(val icon: ImageVector, val destination: Destination, @StringRes val contentDescription: Int)
}

enum class Destination(val route: String, val topBarDetails: TopBarDetails? = null) {
    ONBOARDING(
        route = "onboarding"
    ),
    PREFERENCES(
        route = "preferences",
        topBarDetails = TopBarDetails(
            title = R.string.preferences_title
        )
    ),
    EXPENSES(
        route = "expenses",
        topBarDetails = TopBarDetails(
            title = R.string.app_name,
            hasBackNavigation = false,
            actions = listOf(
                TopBarDetails.Action(Icons.Filled.Settings, PREFERENCES, R.string.preferences_title)
            )
        )
    )
}
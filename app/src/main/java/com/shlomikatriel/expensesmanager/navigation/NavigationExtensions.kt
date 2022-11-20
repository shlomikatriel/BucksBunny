package com.shlomikatriel.expensesmanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.logInfo

@Composable
fun NavHost(navController: NavHostController, startDestination: Destination, builder: NavGraphBuilder.() -> Unit) {
    NavHost(navController = navController, startDestination = startDestination.route, builder = builder)
}

fun NavGraphBuilder.composable(topBarDetailsState: MutableState<TopBarDetails?>, destination: Destination, content: @Composable (NavBackStackEntry) -> Unit) {
    composable(destination.route) {
        topBarDetailsState.value = destination.topBarDetails
        content(it)
    }
}

fun NavController.navigate(destination: Destination) {
    logInfo(Tag.NAVIGATION, "User navigated to destination: ${destination.route}")
    navigate(destination.route)
}

fun NavController.navigate(destination: Destination, builder: NavOptionsBuilder.() -> Unit) {
    logInfo(Tag.NAVIGATION, "User navigated to destination: ${destination.route}")
    navigate(destination.route, builder)
}

fun NavOptionsBuilder.popupTo(destination: Destination, inclusive: Boolean) {
    popUpTo(destination.route) {
        this.inclusive = inclusive
    }
}